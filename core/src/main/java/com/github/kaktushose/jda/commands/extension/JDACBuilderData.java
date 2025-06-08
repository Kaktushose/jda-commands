package com.github.kaktushose.jda.commands.extension;

import com.github.kaktushose.jda.commands.JDACBuilder;
import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.JDAContext;
import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.definitions.description.reflective.ReflectiveDescriptor;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition.CommandConfig;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.expiration.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionControllerInstantiator;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.embeds.Embeds;
import com.github.kaktushose.jda.commands.embeds.error.DefaultErrorMessageFactory;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.extension.Implementation.ExtensionProvidable;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/// Readonly view of a [JDACBuilder]. Acts as a snapshot of the current builder state during jda-commands startup.
///
/// @implNote This class is used to give implementations of [Extension] access to properties involved in the creation of [JDACommands].
///
/// Please note that this class only gives read not write access to the [JDACBuilder].
public sealed class JDACBuilderData permits JDACBuilder {
    public static final Logger log = LoggerFactory.getLogger(JDACBuilderData.class);

    // used for cycling dependency detection
    List<Implementation<?>> alreadyCalled = new ArrayList<>();

    protected final Class<?> baseClass;
    protected final String[] packages;
    protected final JDAContext context;

    // extension stuff
    protected Collection<Extension<Extension.Data>> loadedExtensions = null;
    protected final Map<Class<? extends Extension.Data>, Extension.Data> extensionData = new HashMap<>();
    protected ExtensionFilter extensionFilter = new ExtensionFilter(JDACBuilder.FilterStrategy.EXCLUDE, List.of());

    // loadable by extension
    protected InteractionControllerInstantiator controllerInstantiator = null;

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
    protected CommandConfig globalCommandConfig = new CommandConfig();
    protected LocalizationFunction localizationFunction = ResourceBundleLocalizationFunction.empty().build();
    protected Embeds embeds = Embeds.empty();

    protected JDACBuilderData(Class<?> baseClass, String[] packages, JDAContext context) {
        this.baseClass = baseClass;
        this.packages = packages;
        this.context = context;
        this.classFinders = List.of(ClassFinder.reflective(baseClass, packages));
    }

    @SuppressWarnings("unchecked")
    private Collection<Extension<Extension.Data>> extensions() {
        if (loadedExtensions == null) {
            loadedExtensions = ServiceLoader.load(Extension.class)
                    .stream()
                    .peek(provider -> log.debug("Found extension: {}", provider.type()))
                    .filter(extensionFilter)
                    .peek(provider -> log.debug("Using extension {}", provider.type()))
                    .map(ServiceLoader.Provider::get)
                    .map(extension -> (Extension<Extension.Data>) extension)
                    .peek(extension -> extension.init(extensionData.get(extension.dataType())))
                    .toList();
        }
        return loadedExtensions;
    }

    @SuppressWarnings("unchecked")
    <T extends ExtensionProvidable> SequencedCollection<Map.Entry<Extension<Extension.Data>, T>> implementations(Class<T> type) {
        return extensions()
                .stream()
                .flatMap(extension -> extension.providedImplementations().stream()
                        .filter(implementation -> implementation.type().isAssignableFrom(type))
                        .flatMap(implementation -> implementation.implementations(this).stream())
                        .map(value -> Map.entry(extension, (T) value))
                ).toList();
    }

    private <T extends ExtensionProvidable> T load(Class<T> type, T setValue, T defaultValue) {
        if (setValue != null) return setValue;

        var implementations = implementations(type);

        if (implementations.isEmpty()) {
            if (defaultValue != null) return defaultValue;
            throw new JDACBuilder.ConfigurationException("No implementation for %s found. Please provide!".formatted(type));
        }

        if (implementations.size() == 1) {
            return implementations.getFirst().getValue();
        }

        String foundImplementations = implementations.stream()
                .map(entry -> "extension %s -> %s".formatted(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(System.lineSeparator()));

        throw new JDACBuilder.ConfigurationException(
                "Found multiple implementations of %s, please exclude the unwanted extension: \n%s"
                        .formatted(type, foundImplementations)
        );
    }

    /// the packages provided by the user for classpath scanning
    @NotNull
    public String[] packages() {
        return packages;
    }

    /// the [JDAContext] to be used
    @NotNull
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

    public CommandConfig globalCommandConfig() {
        return globalCommandConfig;
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

    /// @return the [InteractionControllerInstantiator] to be used. Can be added via an [Extension]
    @NotNull
    public InteractionControllerInstantiator controllerInstantiator() {
        return load(InteractionControllerInstantiator.class, controllerInstantiator, null);
    }

    /// @return the [PermissionsProvider] to be used. Can be added via an [Extension]
    @NotNull
    public PermissionsProvider permissionsProvider() {
        return load(PermissionsProvider.class, permissionsProvider, new DefaultPermissionsProvider());
    }

    /// @return the [ErrorMessageFactory] to be used. Can be added via an [Extension]
    @NotNull
    public ErrorMessageFactory errorMessageFactory() {
        return load(ErrorMessageFactory.class, errorMessageFactory, new DefaultErrorMessageFactory(embeds));
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
        Collection<ClassFinder> all = implementations(ClassFinder.class)
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
        all.addAll(classFinders);
        return all;
    }

    /// @return a [ClassFinder] that searches in all [ClassFinder]s returned by [#classFinders()]
    @NotNull
    public ClassFinder mergedClassFinder() {
        Collection<ClassFinder> classFinders = classFinders();

        return annotationClass -> classFinders
                .stream()
                .map(classFinder -> classFinder.search(annotationClass))
                .flatMap(Collection::stream)
                .toList();

    }

    /// @return the [Middleware]s to be used. Can be added via an [Extension]
    @NotNull
    public Collection<Map.Entry<Priority, Middleware>> middlewares() {
        Collection<Map.Entry<Priority, Middleware>> all = implementations(Implementation.MiddlewareContainer.class)
                .stream()
                .map(Map.Entry::getValue)
                .map(container -> Map.entry(container.priority(), container.middleware()))
                .collect(Collectors.toSet());
        all.addAll(middlewares);
        return all;
    }

    /// @return the [Validator]s to be used. Can be added via an [Extension]
    @NotNull
    public Map<Class<? extends Annotation>, Validator> validators() {
        Map<Class<? extends Annotation>, Validator> all = implementations(Implementation.ValidatorContainer.class)
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toMap(Implementation.ValidatorContainer::annotation, Implementation.ValidatorContainer::validator));
        all.putAll(validators);
        return all;
    }

    /// @return the [TypeAdapter]s to be used. Can be added via an [Extension]
    @NotNull
    public Map<Class<?>, TypeAdapter<?>> typeAdapters() {
        Map<Class<?>, TypeAdapter<?>> all = implementations(Implementation.TypeAdapterContainer.class)
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toMap(Implementation.TypeAdapterContainer::type, Implementation.TypeAdapterContainer::adapter));
        all.putAll(typeAdapters);
        return all;
    }
}
