package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.definitions.description.reflective.ReflectiveDescriptor;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dependency.DefaultDependencyInjector;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import com.github.kaktushose.jda.commands.dispatching.expiration.ExpirationStrategy;
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
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.*;

/// This builder is used to build instances of [JDACommands].
/// Please note that values that can be set, have a default implementation by default.
public class JDACommandsBuilder {
    private JDAContext context;
    private LocalizationFunction localizationFunction = ResourceBundleLocalizationFunction.empty().build();
    private DependencyInjector dependencyInjector = new DefaultDependencyInjector();
    private ExpirationStrategy expirationStrategy = ExpirationStrategy.AFTER_15_MINUTES;

    private PermissionsProvider permissionsProvider = new DefaultPermissionsProvider();
    private ErrorMessageFactory errorMessageFactory = new DefaultErrorMessageFactory();
    private GuildScopeProvider guildScopeProvider = new DefaultGuildScopeProvider();

    private final Collection<ClassFinder> classFinders;
    private Descriptor descriptor = new ReflectiveDescriptor();

    // registries
    private final Map<Class<?>, TypeAdapter<?>> typeAdapters = new HashMap<>();
    private final Set<Map.Entry<Priority, Middleware>> middlewares = new HashSet<>();
    private final Map<Class<? extends Annotation>, Validator> validators = new HashMap<>();

    JDACommandsBuilder(JDAContext context, ClassFinder[] classFinders) {
        this.classFinders = Arrays.asList(classFinders);
        this.context = Objects.requireNonNull(context);
    }

    public JDACommandsBuilder jda(JDA jda) {
        this.context = new JDAContext(jda);
        return this;
    }

    /// Adds instanced of [ClassFinder] to the later used collection
    /// @param classFinders The [ClassFinder]s to be added
    public JDACommandsBuilder classFinders(ClassFinder... classFinders) {
        this.classFinders.addAll(Arrays.asList(classFinders));
        return this;
    }

    public JDACommandsBuilder descriptor(Descriptor descriptor) {
        this.descriptor = descriptor;
        return this;
    }

    /// @param shardManager The [ShardManager] to be used by JDA-Commands
    public JDACommandsBuilder shardManager(ShardManager shardManager) {
        this.context = new JDAContext(shardManager);
        return this;
    }

    /// @param localizationFunction The [LocalizationFunction] to be used to localize things
    public JDACommandsBuilder localizationFunction(LocalizationFunction localizationFunction) {
        this.localizationFunction = Objects.requireNonNull(localizationFunction);
        return this;
    }

    /// @param dependencyInjector The [DependencyInjector] that should be used to instantiate instances of the user defined Interactions [Interaction]
    public JDACommandsBuilder dependencyInjector(DependencyInjector dependencyInjector) {
        this.dependencyInjector = Objects.requireNonNull(dependencyInjector);
        return this;
    }

    /// @param expirationStrategy The [ExpirationStrategy] to be used
    public JDACommandsBuilder expirationStrategy(ExpirationStrategy expirationStrategy) {
        this.expirationStrategy = Objects.requireNonNull(expirationStrategy);
        return this;
    }

    /// @param priority The [Priority] with what the [Middleware] should be registered
    /// @param middleware The to be registered [Middleware]
    public JDACommandsBuilder middleware(Priority priority, Middleware middleware) {
        Objects.requireNonNull(priority);
        Objects.requireNonNull(middleware);

        middlewares.add(Map.entry(priority, middleware));
        return this;
    }

    /// @param type The resulting type of the give [TypeAdapter]
    /// @param adapter The [TypeAdapter] to be registered
    public JDACommandsBuilder adapter(Class<?> type, @NotNull TypeAdapter<?> adapter) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(adapter);

        typeAdapters.put(type, adapter);
        return this;
    }

    /// @param annotation The annotation for which the given [Validator] should be called
    /// @param validator The to be registered [Validator]
    public JDACommandsBuilder validator(Class<? extends Annotation> annotation, @NotNull Validator validator) {
        Objects.requireNonNull(annotation);
        Objects.requireNonNull(validator);

        validators.put(annotation, validator);
        return this;
    }

    /// @param permissionsProvider The [PermissionsProvider] that should be used
    public JDACommandsBuilder permissionsProvider(PermissionsProvider permissionsProvider) {
        this.permissionsProvider = Objects.requireNonNull(permissionsProvider);
        return this;
    }

    /// @param errorMessageFactory The [ErrorMessageFactory] that should be used
    public JDACommandsBuilder errorMessageFactory(ErrorMessageFactory errorMessageFactory) {
        this.errorMessageFactory = Objects.requireNonNull(errorMessageFactory);
        return this;
    }

    /// @param guildScopeProvider The [GuildScopeProvider] that should be used
    public JDACommandsBuilder guildScopeProvider(GuildScopeProvider guildScopeProvider) {
        this.guildScopeProvider = Objects.requireNonNull(guildScopeProvider);
        return this;
    }

    /// This method instantiated an instance of [JDACommands] and starts the framework.
    public JDACommands start(Class<?> clazz, String... packages) {
        Validators validators = new Validators(this.validators);
        JDACommands jdaCommands = new JDACommands(
                context,
                dependencyInjector,
                expirationStrategy,
                new TypeAdapters(typeAdapters),
                new Middlewares(middlewares, errorMessageFactory, permissionsProvider),
                errorMessageFactory,
                guildScopeProvider,
                new InteractionRegistry(dependencyInjector, validators, localizationFunction, descriptor)
        );

        return jdaCommands.start(classFinders, clazz, packages);
    }
}
