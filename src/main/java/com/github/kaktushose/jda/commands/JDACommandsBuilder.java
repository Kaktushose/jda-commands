package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.dependency.DefaultDependencyInjector;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.internal.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.ValidatorRegistry;
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

public class JDACommandsBuilder {
    private Class<?> clazz;
    private String[] packages;
    private JDAContext context;
    private LocalizationFunction localizationFunction = ResourceBundleLocalizationFunction.empty().build();
    private DependencyInjector dependencyInjector = new DefaultDependencyInjector();
    private ExpirationStrategy expirationStrategy = ExpirationStrategy.AFTER_15_MINUTES;

    private PermissionsProvider permissionsProvider = new DefaultPermissionsProvider();
    private ErrorMessageFactory errorMessageFactory = new DefaultErrorMessageFactory();
    private GuildScopeProvider guildScopeProvider = new DefaultGuildScopeProvider();
    private final Map<Class<?>, TypeAdapter<?>> typeAdapters = new HashMap<>();

    private final Map<Priority, Collection<Middleware>> middlewares = Map.of(
            Priority.PERMISSIONS, new HashSet<>(),
            Priority.HIGH, new HashSet<>(),
            Priority.NORMAL, new HashSet<>(),
            Priority.LOW, new HashSet<>()
    );

    private final Map<Class<? extends Annotation>, Validator> validators = new HashMap<>();

    JDACommandsBuilder(JDAContext context, Class<?> clazz, String[] packages) {
        this.clazz = Objects.requireNonNull(clazz);
        this.context = Objects.requireNonNull(context);
        this.packages = Objects.requireNonNull(packages);
    }

    public JDACommandsBuilder jda(JDA jda) {
        this.context = new JDAContext(jda);
        return this;
    }

    public JDACommandsBuilder shardManager(ShardManager shardManager) {
        this.context = new JDAContext(shardManager);
        return this;
    }

    public JDACommandsBuilder packages(String[] packages) {
        this.packages = Objects.requireNonNull(packages);
        return this;
    }

    public JDACommandsBuilder clazz(Class<?> clazz) {
        this.clazz = Objects.requireNonNull(clazz);;
        return this;
    }

    public JDACommandsBuilder localizationFunction(LocalizationFunction localizationFunction) {
        this.localizationFunction = Objects.requireNonNull(localizationFunction);
        return this;
    }

    public JDACommandsBuilder dependencyInjector(DependencyInjector dependencyInjector) {
        this.dependencyInjector = Objects.requireNonNull(dependencyInjector);
        return this;
    }

    public JDACommandsBuilder expirationStrategy(ExpirationStrategy expirationStrategy) {
        this.expirationStrategy = Objects.requireNonNull(expirationStrategy);
        return this;
    }

    public JDACommandsBuilder middleware(Priority priority, Middleware middleware) {
        Objects.requireNonNull(priority);
        Objects.requireNonNull(middleware);

        middlewares.get(priority).add(middleware);
        return this;
    }

    public JDACommandsBuilder adapter(Class<?> type, @NotNull TypeAdapter<?> adapter) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(adapter);

        typeAdapters.put(type, adapter);
        return this;
    }

    public JDACommandsBuilder validator(Class<? extends Annotation> annotation, @NotNull Validator validator) {
        Objects.requireNonNull(annotation);
        Objects.requireNonNull(validator);

        validators.put(annotation, validator);
        return this;
    }

    public JDACommandsBuilder permissionsProvider(PermissionsProvider permissionsProvider) {
        this.permissionsProvider = Objects.requireNonNull(permissionsProvider);
        return this;
    }

    public JDACommandsBuilder errorMessageFactory(ErrorMessageFactory errorMessageFactory) {
        this.errorMessageFactory = Objects.requireNonNull(errorMessageFactory);
        return this;
    }

    public JDACommandsBuilder guildScopeProvider(GuildScopeProvider guildScopeProvider) {
        this.guildScopeProvider = Objects.requireNonNull(guildScopeProvider);
        return this;
    }

    public JDACommands start() {
        JDACommands jdaCommands = new JDACommands(
                context,
                dependencyInjector,
                localizationFunction,
                expirationStrategy
        );

        jdaCommands.implementationRegistry().setPermissionsProvider(permissionsProvider);
        jdaCommands.implementationRegistry().setGuildScopeProvider(guildScopeProvider);
        jdaCommands.implementationRegistry().setErrorMessageFactory(errorMessageFactory);

        ValidatorRegistry validatorRegistry = jdaCommands.validatorRegistry();
        validators.forEach(validatorRegistry::register);

        TypeAdapterRegistry typeAdapterRegistry = jdaCommands.adapterRegistry();
        typeAdapters.forEach(typeAdapterRegistry::register);

        MiddlewareRegistry middlewareRegistry = jdaCommands.middlewareRegistry();
        middlewares.forEach(middlewareRegistry::register);


        return jdaCommands.start(clazz, packages);
    }
}
