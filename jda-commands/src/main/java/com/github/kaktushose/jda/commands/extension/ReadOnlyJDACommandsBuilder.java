package com.github.kaktushose.jda.commands.extension;

import com.github.kaktushose.jda.commands.JDACommandsBuilder;
import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.definitions.description.reflective.ReflectiveDescriptor;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.expiration.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.instance.InstanceProvider;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.embeds.error.DefaultErrorMessageFactory;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.internal.JDAContext;
import com.github.kaktushose.jda.commands.permissions.DefaultPermissionsProvider;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import com.github.kaktushose.jda.commands.scope.DefaultGuildScopeProvider;
import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public sealed class ReadOnlyJDACommandsBuilder permits JDACommandsBuilder {
    public static final Logger log = LoggerFactory.getLogger(ReadOnlyJDACommandsBuilder.class);

    protected Collection<Extension> loadedExtensions = null;
    protected final Map<Class<? extends Extension.Data>, Extension.Data> extensionData = new HashMap<>();
    protected ExtensionFilter extensionFilter = new ExtensionFilter(JDACommandsBuilder.FilterStrategy.EXCLUDE, List.of());

    protected final Class<?> baseClass;
    protected final String[] packages;
    protected final JDAContext context;

    // loadable by extension
    protected LocalizationFunction localizationFunction;
    protected InstanceProvider instanceProvider = null;

    protected ExpirationStrategy expirationStrategy = ExpirationStrategy.AFTER_15_MINUTES;

    protected PermissionsProvider permissionsProvider = null;
    protected ErrorMessageFactory errorMessageFactory = null;
    protected GuildScopeProvider guildScopeProvider = null;
    protected Descriptor descriptor = null;

    // loadable by extensions (addition)
    protected Collection<ClassFinder> classFinders;
    protected final Set<Map.Entry<Priority, Middleware>> middlewares = new HashSet<>();
    protected final Map<Class<? extends Annotation>, Validator> validators = new HashMap<>();
    protected final Map<Class<?>, TypeAdapter<?>> typeAdapters = new HashMap<>();

    // only user settable
    protected InteractionDefinition.ReplyConfig globalReplyConfig = new InteractionDefinition.ReplyConfig();

    public ReadOnlyJDACommandsBuilder(Class<?> baseClass, String[] packages, JDAContext context) {
        this.baseClass = baseClass;
        this.packages = packages;
        this.context = context;
        this.classFinders = List.of(ClassFinder.reflective(baseClass, packages));
    }

    private Collection<Extension> extensions() {
        if (loadedExtensions == null) {
            loadedExtensions = ServiceLoader.load(Extension.class)
                    .stream()
                    .peek(provider -> log.debug("Found extension: {}", provider.type()))
                    .filter(extensionFilter)
                    .peek(provider -> log.debug("Using extension {}", provider.type()))
                    .map(ServiceLoader.Provider::get)
                    .peek(extension -> extension.init(this, extensionData.get(extension.dataType())))
                    .toList();
        }
        return loadedExtensions;
    }

    private <T> List<Map.Entry<Extension, T>> implementation(Class<T> type) {
        return extensions()
                .stream()
                .flatMap(extension -> extension.providedImplementations()
                        .stream()
                        .filter(type::isInstance)
                        .map(type::cast)
                        .map(impl -> Map.entry(extension, impl))
                )
                .toList();
    }

    private <T> T load(Class<T> type, T setValue, T defaultValue) {
        if (setValue != null) return setValue;
        List<Map.Entry<Extension, T>> implementations = implementation(type);

        if (implementations.isEmpty()) {
            if (defaultValue != null) return defaultValue;
            throw new JDACommandsBuilder.ConfigurationException("No implementation for %s found. Please provide!".formatted(type));
        } else if (implementations.size() == 1) {
            return implementations.getFirst().getValue();
        } else {
            String foundImplementations = implementations.stream()
                    .map(entry -> "extension %s -> %s".formatted(entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(System.lineSeparator()));

            throw new JDACommandsBuilder.ConfigurationException(
                    "Found multiple implementations of %s, please exclude the unwanted extension: \n%s"
                            .formatted(type, foundImplementations)
            );
        }

    }

    public String[] packages() {
        return packages;
    }

    public JDAContext context() {
        return context;
    }

    public Class<?> baseClass() {
        return baseClass;
    }

    public InteractionDefinition.ReplyConfig globalReplyConfig() {
        return globalReplyConfig;
    }

    // loaded
    public LocalizationFunction localizationFunction() {
        return load(LocalizationFunction.class, localizationFunction, ResourceBundleLocalizationFunction.empty().build());
    }

    public InstanceProvider instanceProvider() {
        return load(InstanceProvider.class, instanceProvider, null);
    }

    public ExpirationStrategy expirationStrategy() {
        return expirationStrategy;
    }

    public PermissionsProvider permissionsProvider() {
        return load(PermissionsProvider.class, permissionsProvider, new DefaultPermissionsProvider());
    }

    public ErrorMessageFactory errorMessageFactory() {
        return load(ErrorMessageFactory.class, errorMessageFactory, new DefaultErrorMessageFactory());
    }

    public GuildScopeProvider guildScopeProvider() {
        return load(GuildScopeProvider.class, guildScopeProvider, new DefaultGuildScopeProvider());
    }

    public Descriptor descriptor() {
        return load(Descriptor.class, descriptor, new ReflectiveDescriptor());
    }

    public Collection<ClassFinder> classFinders() {
        Collection<ClassFinder> all = implementation(ClassFinder.class)
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
        all.addAll(classFinders);
        return all;
    }

    public Collection<Map.Entry<Priority, Middleware>> middlewares() {
        @SuppressWarnings("unchecked")
        Collection<Map.Entry<Priority, Middleware>> all = implementation(Map.Entry.class)
                .stream()
                .map(Map.Entry::getValue)
                .filter(entry -> entry.getKey() instanceof Priority && entry.getValue() instanceof Middleware)
                .map(entry -> (Map.Entry<Priority, Middleware>) entry)
                .collect(Collectors.toSet());
        all.addAll(middlewares);
        return all;
    }

    public Map<Class<? extends Annotation>, Validator> validators() {
        @SuppressWarnings("unchecked")
        Map<Class<? extends Annotation>, Validator> all = implementation(Map.Entry.class)
                .stream()
                .map(Map.Entry::getValue)
                .filter(entry -> entry.getKey() instanceof Class && entry.getValue() instanceof Validator)
                .map(entry -> (Map.Entry<Class<? extends Annotation>, Validator>) entry)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        all.putAll(validators);
        return all;
    }

    public Map<Class<?>, TypeAdapter<?>> typeAdapters() {
        @SuppressWarnings("unchecked")
        Map<Class<?>, TypeAdapter<?>> all = implementation(Map.Entry.class)
                .stream()
                .map(Map.Entry::getValue)
                .filter(entry -> entry.getKey() instanceof Class && entry.getValue() instanceof TypeAdapter<?>)
                .map(entry -> (Map.Entry<Class<?>, TypeAdapter<?>>) entry)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        all.putAll(typeAdapters);
        return all;
    }
}
