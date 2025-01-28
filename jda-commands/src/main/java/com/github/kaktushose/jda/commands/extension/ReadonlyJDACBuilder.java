package com.github.kaktushose.jda.commands.extension;

import com.github.kaktushose.jda.commands.JDACommandsBuilder;
import com.github.kaktushose.jda.commands.JDAContext;
import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.definitions.description.reflective.ReflectiveDescriptor;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.expiration.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionClassProvider;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.embeds.error.DefaultErrorMessageFactory;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.extension.internal.ExtensionFilter;
import com.github.kaktushose.jda.commands.permissions.DefaultPermissionsProvider;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import com.github.kaktushose.jda.commands.scope.DefaultGuildScopeProvider;
import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// This class is used to give implementations of [Extension] access to properties involved in the creation of [com.github.kaktushose.jda.commands.JDACommands]
///
/// Please note that this class only give read not write access to the [JDACommandsBuilder]
public sealed class ReadonlyJDACBuilder permits JDACommandsBuilder {
    public static final Logger log = LoggerFactory.getLogger(ReadonlyJDACBuilder.class);

    // used for cycling dependency detection
    List<Implementation<?>> alreadyCalled = new ArrayList<>();

    protected final Class<?> baseClass;
    protected final String[] packages;
    protected final JDAContext context;

    // extension stuff
    protected Collection<Extension> loadedExtensions = null;
    protected final Map<Class<? extends Extension.Data>, Extension.Data> extensionData = new HashMap<>();
    protected ExtensionFilter extensionFilter = new ExtensionFilter(JDACommandsBuilder.FilterStrategy.EXCLUDE, List.of());


    // loadable by extension
    protected InteractionClassProvider instanceProvider = null;

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
    protected LocalizationFunction localizationFunction = ResourceBundleLocalizationFunction.empty().build();

    protected ReadonlyJDACBuilder(Class<?> baseClass, String[] packages, JDAContext context) {
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
                    .peek(extension -> extension.init(extensionData.get(extension.dataType())))
                    .toList();
        }
        return loadedExtensions;
    }

    @SuppressWarnings("unchecked")
    <T extends Implementation.ExtensionImplementable> SequencedCollection<Map.Entry<Extension, T>> implementation(Class<T> type) {
        return extensions()
                .stream()
                .flatMap(extension ->
                        extension.providedImplementations()
                                .stream()
                                .filter(provider -> provider.type().isAssignableFrom(type))
                                .map(implementation -> implementation.getValue(this))
                                .filter(Objects::nonNull)
                                .map(value -> Map.entry(extension, (T) value))
                )
                .toList();
    }

    private <T extends Implementation.ExtensionImplementable> T load(Class<T> type, T setValue, T defaultValue) {
        if (setValue != null) return setValue;
        SequencedCollection<Map.Entry<Extension, T>> implementations = implementation(type);

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

    /// the packages provided by the user for classpath scanning
    @NotNull
    public String[] packages() {
        return packages;
    }

    /// the [JDAContext] to be used
    public JDAContext context() {
        return context;
    }

    /// the base class provided by the user for classpath scanning
    @NotNull
    public Class<?> baseClass() {
        return baseClass;
    }

    /// @return the global [InteractionDefinition.ReplyConfig] provided by the user
    @NotNull
    public InteractionDefinition.ReplyConfig globalReplyConfig() {
        return globalReplyConfig;
    }

    /// @return the [ExpirationStrategy] to be used
    @NotNull
    public ExpirationStrategy expirationStrategy() {
        return expirationStrategy;
    }

    // will be later loadable
    /// @return the [LocalizationFunction] to be used. Can be added via an [Extension]
    @NotNull
    public LocalizationFunction localizationFunction() {
        return localizationFunction;
    }

    // loadable
    /// @return the [InteractionClassProvider] to be used. Can be added via an [Extension]
    @NotNull
    public InteractionClassProvider instanceProvider() {
        return load(InteractionClassProvider.class, instanceProvider, null);
    }

    /// @return the [PermissionsProvider] to be used. Can be added via an [Extension]
    @NotNull
    public PermissionsProvider permissionsProvider() {
        return load(PermissionsProvider.class, permissionsProvider, new DefaultPermissionsProvider());
    }

    /// @return the [ErrorMessageFactory] to be used. Can be added via an [Extension]
    @NotNull
    public ErrorMessageFactory errorMessageFactory() {
        return load(ErrorMessageFactory.class, errorMessageFactory, new DefaultErrorMessageFactory());
    }

    /// @return the [GuildScopeProvider] to be used. Can be added via an [Extension]
    @NotNull
    public GuildScopeProvider guildScopeProvider() {
        return load(GuildScopeProvider.class, guildScopeProvider, new DefaultGuildScopeProvider());
    }

    /// @return the [Descriptor] to be used. Can be added via an [Extension]
    @NotNull
    public Descriptor descriptor() {
        return load(Descriptor.class, descriptor, new ReflectiveDescriptor());
    }

    /// @return the [ClassFinder]s to be used. Can be added via an [Extension]
    @NotNull
    public Collection<ClassFinder> classFinders() {
        Collection<ClassFinder> all = implementation(ClassFinder.class)
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
        all.addAll(classFinders);
        return all;
    }

    /// @return an instance of [ClassFinder] that searches in all [ClassFinder]s returned by [#classFinders()].
    public ClassFinder mergedClassFinder() {
        Collection<ClassFinder> classFinders = classFinders();

        return annotationClass -> classFinders
                 .stream()
                 .map(classFinder -> classFinder.search(annotationClass))
                 .flatMap(Collection::stream)
                 .toList();

    }

    @SuppressWarnings("unchecked")
    private <K, V> Stream<Map.Entry<K, V>> mapEntryImplementations(Class<K> keyType, Class<V> valueType) {
        return implementation(Implementation.Pair.class)
                .stream()
                .map(Map.Entry::getValue)
                .filter(entry -> keyType.isInstance(entry.key()) && valueType.isInstance(entry.value()))
                .map(entry -> (Map.Entry<K, V>) Map.entry(entry.key(), entry.value()));
    }

    /// @return the [Middleware]s to be used. Can be added via an [Extension]
    @NotNull
    public Collection<Map.Entry<Priority, Middleware>> middlewares() {
        Collection<Map.Entry<Priority, Middleware>> all = mapEntryImplementations(Priority.class, Middleware.class)
                .collect(Collectors.toSet());
        all.addAll(middlewares);
        return all;
    }

    /// @return the [Validator]s to be used. Can be added via an [Extension]
    @NotNull
    public Map<Class<? extends Annotation>, Validator> validators() {
        @SuppressWarnings("unchecked")
        Map<Class<? extends Annotation>, Validator> all = mapEntryImplementations(Class.class, Validator.class)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        all.putAll(validators);
        return all;
    }

    /// @return the [TypeAdapter]s to be used. Can be added via an [Extension]
    @NotNull
    public Map<Class<?>, TypeAdapter<?>> typeAdapters() {
        @SuppressWarnings("unchecked")
        Map<Class<?>, TypeAdapter<?>> all = mapEntryImplementations(Class.class, TypeAdapter.class)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        all.putAll(typeAdapters);
        return all;
    }
}
