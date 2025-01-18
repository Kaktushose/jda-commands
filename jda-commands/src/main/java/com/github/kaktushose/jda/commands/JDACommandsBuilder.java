package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition.ReplyConfig;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import com.github.kaktushose.jda.commands.dispatching.expiration.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.instance.InstanceProvider;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.middleware.internal.Middlewares;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.Validators;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.extension.Extension;
import com.github.kaktushose.jda.commands.extension.ExtensionFilter;
import com.github.kaktushose.jda.commands.extension.ReadOnlyJDACommandsBuilder;
import com.github.kaktushose.jda.commands.internal.JDAContext;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;

/// This builder is used to build instances of [JDACommands].
///
/// Please note that values that can be set have a default implementation;
/// These default implementations are sometimes based on reflections. If you want to avoid reflections, you have to provide your own implementations for:
/// - [#descriptor(com.github.kaktushose.jda.commands.definitions.description.Descriptor)]
/// - [#classFinders(ClassFinder...)]
/// - [#instanceProvider(InstanceProvider)]
///
///
/// In addition to manually configuring this builder, you can also provide implementations of [Extension] trough java's [`service
/// provider interface`][ServiceLoader], which are then applied by [#start()] or [#applyExtensions(FilterStrategy, String...)].
///
/// These implementations of [Extension] can be additionally configured by adding the according implementations of [Extension.Data]
/// by calling [#extensionData(Extension.Data...)]. (if supported by the extension)
///
///
/// ## Example
/// ```java
/// JDACommands jdaCommands = JDACommands.builder(jda, Main.class)
///     .middleware(Priority.NORMAL, new TestMiddleware())
///     .globalReplyConfig(new InteractionDefinition.ReplyConfig(false, false, true))
///     .classFinders(ClassFinder.reflective(Main.class), ClassFinders.explicit(ButtonInteraction.class))
///     .start();
/// ```
/// @see Extension
public final class JDACommandsBuilder extends ReadOnlyJDACommandsBuilder {
    private static final Logger log = LoggerFactory.getLogger(JDACommandsBuilder.class);

    JDACommandsBuilder(@NotNull JDAContext context, @NotNull Class<?> baseClass, @NotNull String[] packages) {
        super(baseClass, packages, context);
    }


    /// @param classFinders the to be used [ClassFinder]s
    ///
    /// @apiNote This method overrides the underlying collection instead of adding to it.
    /// If you want to add own [ClassFinder]s while keeping the default reflective implementation, you have to add it explicitly via
    /// [ClassFinder#reflective(Class, String...)] too.
    ///
    @NotNull
    public JDACommandsBuilder classFinders(@NotNull ClassFinder... classFinders) {
        this.classFinders = new ArrayList<>(Arrays.asList(classFinders));
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

    /// @param instanceProvider the implementation of [InstanceProvider] to be used.
    public JDACommandsBuilder instanceProvider(InstanceProvider instanceProvider) {
        this.instanceProvider = instanceProvider;
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

    /// Registers instances of implementations of [Extension.Data] to be used by the according implementation
    /// of [Extension] to configure it properly.
    ///
    /// @param data the instances of [Extension.Data] to be used
    public JDACommandsBuilder extensionData(Extension.Data... data) {
        for (Extension.Data entity : data) {
            extensionData.put(entity.getClass(), entity);
        }
        return this;
    }

    /// After filtering the by SPI registered implementations of [Extension] according to the chosen [FilterStrategy],
    /// this method loads all implementations of [Extension] by executing [Extension#configure(JDACommandsBuilder, Extension.Data)].
    ///
    /// If this method is not called explicitly all [Extension]s will be loaded by [#start()].
    ///
    /// This methods should only be used if you want to filter some implementations of [Extension] or if you want to override
    /// some values set by any [Extension#configure(JDACommandsBuilder, Extension.Data)].
    ///
    /// @apiNote This method compares the [`fully classified class name`][Class#getName()] of all [Extension] implementations by using [String#startsWith(String)],
    /// so it's possible to include/exclude a bunch of classes in the same package by just providing the package name.
    ///
    /// @param strategy the filtering strategy to be used either [FilterStrategy#INCLUDE] or [FilterStrategy#EXCLUDE]
    /// @param classes the classes to be filtered
    public JDACommandsBuilder filterExtensions(FilterStrategy strategy, String... classes) {
        this.extensionFilter = new ExtensionFilter(strategy, Arrays.asList(classes));
        return this;
    }

    /// The two available filter strategies
    public enum FilterStrategy {
        /// includes the defined classes
        INCLUDE,
        /// excludes the defined classes
        EXCLUDE
    }

    /// This method applies all found implementations of [Extension] (if [#applyExtensions(FilterStrategy, String...)] isn't called explicitly),
    /// instantiates an instance of [JDACommands] and starts the framework.
    @NotNull
    public JDACommands start() {

        ErrorMessageFactory errorMessageFactory = errorMessageFactory();
        JDACommands jdaCommands = new JDACommands(
                context(),
                expirationStrategy(),
                new TypeAdapters(typeAdapters()),
                new Middlewares(middlewares(), errorMessageFactory, permissionsProvider()),
                errorMessageFactory,
                guildScopeProvider(),
                new InteractionRegistry(new Validators(validators()), localizationFunction(), descriptor()),
                instanceProvider(),
                globalReplyConfig()
                );
        loadedExtensions.forEach(extension -> extension.afterFrameworkInit(jdaCommands));

        jdaCommands.start(classFinders(), baseClass(), packages());

        loadedExtensions.forEach(extension -> extension.afterFrameworkStart(jdaCommands));
        return jdaCommands;
    }

    /// Will be thrown if anything goes wrong while configuring jda-commands.
    public static class ConfigurationException extends RuntimeException {
        public ConfigurationException(String message) {
            super("Error while trying to configure jda-commands: " + message);
        }
    }
}
