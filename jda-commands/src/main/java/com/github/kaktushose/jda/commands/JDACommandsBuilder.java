package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.definitions.description.reflective.ReflectiveDescriptor;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition.ReplyConfig;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import com.github.kaktushose.jda.commands.dispatching.expiration.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.instantiation.Instantiator;
import com.github.kaktushose.jda.commands.dispatching.instantiation.spi.InstantiatorProvider;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.middleware.internal.Middlewares;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.Validators;
import com.github.kaktushose.jda.commands.embeds.error.DefaultErrorMessageFactory;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.internal.JDAContext;
import com.github.kaktushose.jda.commands.permissions.DefaultPermissionsProvider;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import com.github.kaktushose.jda.commands.scope.DefaultGuildScopeProvider;
import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.*;

/// This builder is used to build instances of [JDACommands].
///
/// Please note that values that can be set have a default implementation;
/// These default implementations are sometimes bases on reflections. If you want to avoid reflections, you have to provide your own implementations for:
/// - [#descriptor(com.github.kaktushose.jda.commands.definitions.description.Descriptor)]
/// - [#classFinders(ClassFinder...)]
///
/// ## Example
/// ```java
/// JDACommands jdaCommands = JDACommands.builder(jda, Main.class)
///     .middleware(Priority.NORMAL, new TestMiddleware())
///     .globalReplyConfig(new InteractionDefinition.ReplyConfig(false, false, true))
///     .classFinders(ClassFinder.reflective(Main.class), ClassFinders.explicit(ButtonInteraction.class))
///     .start();
/// ```
///
public class JDACommandsBuilder {
    private final Class<?> baseClass;
    private final String[] packages;
    private final JDAContext context;

    private LocalizationFunction localizationFunction = ResourceBundleLocalizationFunction.empty().build();
    private Instantiator instantiator = null;
    private InstantiatorProvider.Data instatiatorProviderData = null;

    private ExpirationStrategy expirationStrategy = ExpirationStrategy.AFTER_15_MINUTES;

    private PermissionsProvider permissionsProvider = new DefaultPermissionsProvider();
    private ErrorMessageFactory errorMessageFactory = new DefaultErrorMessageFactory();
    private GuildScopeProvider guildScopeProvider = new DefaultGuildScopeProvider();

    private Collection<ClassFinder> classFinders;
    private Descriptor descriptor = new ReflectiveDescriptor();

    private ReplyConfig globalReplyConfig = new ReplyConfig();

    // registries
    private final Map<Class<?>, TypeAdapter<?>> typeAdapters = new HashMap<>();
    private final Set<Map.Entry<Priority, Middleware>> middlewares = new HashSet<>();
    private final Map<Class<? extends Annotation>, Validator> validators = new HashMap<>();


    JDACommandsBuilder(@NotNull JDAContext context, @NotNull Class<?> baseClass, @NotNull String[] packages) {
        this.baseClass = baseClass;
        this.packages = packages;
        this.context = Objects.requireNonNull(context);
        this.classFinders = List.of(ClassFinder.reflective(baseClass, packages));
    }


    /// @param classFinders the to be used [ClassFinder]s
    ///
    /// @apiNote This method overrides the underlying collection instead of adding to it.
    /// If you want to add own [ClassFinder]s while keeping the default reflective implementation, you have to add it explicitly via
    /// [ClassFinder#reflective(Class, String...)] too.
    ///
    @NotNull
    public JDACommandsBuilder classFinders(@NotNull ClassFinder... classFinders) {
        this.classFinders = Arrays.asList(classFinders);
        return this;
    }

    /// @param descriptor the [Descriptor] to be used
    @NotNull
    public JDACommandsBuilder descriptor(@NotNull Descriptor descriptor) {
        this.descriptor = descriptor;
        return this;
    }

    /// @param localizationFunction The [LocalizationFunction] to be used to localize interactions
    @NotNull
    public JDACommandsBuilder localizationFunction(@NotNull LocalizationFunction localizationFunction) {
        this.localizationFunction = Objects.requireNonNull(localizationFunction);
        return this;
    }

    /// @param instantiator The [Instantiator] to be used to instantiate interaction classes
    public JDACommandsBuilder instantiator(Instantiator instantiator) {
        this.instantiator = instantiator;
        return this;
    }

    public JDACommandsBuilder instantiatorProviderData(InstantiatorProvider.Data instatiatorProviderData) {
        this.instatiatorProviderData = instatiatorProviderData;
        return this;
    }

    /// @param expirationStrategy The [ExpirationStrategy] to be used
    @NotNull
    public JDACommandsBuilder expirationStrategy(@NotNull ExpirationStrategy expirationStrategy) {
        this.expirationStrategy = Objects.requireNonNull(expirationStrategy);
        return this;
    }

    /// @param priority   The [Priority] with what the [Middleware] should be registered
    /// @param middleware The to be registered [Middleware]
    @NotNull
    public JDACommandsBuilder middleware(@NotNull Priority priority, @NotNull Middleware middleware) {
        Objects.requireNonNull(priority);
        Objects.requireNonNull(middleware);

        middlewares.add(Map.entry(priority, middleware));
        return this;
    }

    /// @param type    The type that the given [TypeAdapter] can handle
    /// @param adapter The [TypeAdapter] to be registered
    @NotNull
    public JDACommandsBuilder adapter(@NotNull Class<?> type, @NotNull TypeAdapter<?> adapter) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(adapter);

        typeAdapters.put(type, adapter);
        return this;
    }

    /// @param annotation The annotation for which the given [Validator] should be called
    /// @param validator  The [Validator] to be registered
    @NotNull
    public JDACommandsBuilder validator(@NotNull Class<? extends Annotation> annotation, @NotNull Validator validator) {
        Objects.requireNonNull(annotation);
        Objects.requireNonNull(validator);

        validators.put(annotation, validator);
        return this;
    }

    /// @param permissionsProvider The [PermissionsProvider] that should be used
    @NotNull
    public JDACommandsBuilder permissionsProvider(@NotNull PermissionsProvider permissionsProvider) {
        this.permissionsProvider = Objects.requireNonNull(permissionsProvider);
        return this;
    }

    /// @param errorMessageFactory The [ErrorMessageFactory] that should be used
    @NotNull
    public JDACommandsBuilder errorMessageFactory(@NotNull ErrorMessageFactory errorMessageFactory) {
        this.errorMessageFactory = Objects.requireNonNull(errorMessageFactory);
        return this;
    }

    /// @param guildScopeProvider The [GuildScopeProvider] that should be used
    @NotNull
    public JDACommandsBuilder guildScopeProvider(@NotNull GuildScopeProvider guildScopeProvider) {
        this.guildScopeProvider = Objects.requireNonNull(guildScopeProvider);
        return this;
    }

    /// @param globalReplyConfig the [ReplyConfig] to be used as a global fallback option
    @NotNull
    public JDACommandsBuilder globalReplyConfig(@NotNull ReplyConfig globalReplyConfig) {
        this.globalReplyConfig = globalReplyConfig;
        return this;
    }

    /// This method instantiates an instance of [JDACommands] and starts the framework.
    @NotNull
    public JDACommands start() {
        if (instantiator == null) {
            instantiator = findDefaultInstantiator();
        }

        JDACommands jdaCommands = new JDACommands(
                context,
                expirationStrategy,
                new TypeAdapters(typeAdapters),
                new Middlewares(middlewares, errorMessageFactory, permissionsProvider),
                errorMessageFactory,
                guildScopeProvider,
                new InteractionRegistry(new Validators(this.validators), localizationFunction, descriptor),
                instantiator,
                globalReplyConfig
                );

        return jdaCommands.start(classFinders, baseClass, packages);
    }

    private Instantiator findDefaultInstantiator() {
        return ServiceLoader.load(InstantiatorProvider.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .max(Comparator.comparingInt(InstantiatorProvider::priority))
                .orElseThrow(() -> new IllegalStateException(
                        "No InstantiatorProvider was found! Please use a default integration provided by jda-commands like the guice integration or write your own.")
                )
                .create(instatiatorProviderData);
    }
}
