package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition.ReplyConfig;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import com.github.kaktushose.jda.commands.dispatching.expiration.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionControllerInstantiator;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.middleware.internal.Middlewares;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.Validators;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.extension.Extension;
import com.github.kaktushose.jda.commands.extension.JDACBuilderData;
import com.github.kaktushose.jda.commands.extension.internal.ExtensionFilter;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.*;

/// This builder is used to build instances of [JDACommands].
///
/// Please note that values that can be set have a default implementation.
/// These following implementations are based on reflections. If you want to avoid reflections, you have to provide your own implementations for:
///
/// - [#descriptor(Descriptor)]
/// - [#classFinders(ClassFinder...)]
/// - [#instanceProvider(InteractionControllerInstantiator)]
///
///
/// In addition to manually configuring this builder, you can also provide implementations of [Extension] trough Javas [`service
/// provider interface`][ServiceLoader], which are applied during [JDACommands] creation.
/// Values manually defined by this builder will always override loaded and default ones, except for:
///
/// - [#middleware(Priority, Middleware)]
/// - [#adapter(Class, TypeAdapter)]
/// - [#validator(Class, Validator)]
///
/// which will add to the default and loaded ones.
///
/// These implementations of [Extension] can be additionally configured by adding the according implementation of [Extension.Data]
/// by calling [#extensionData(Extension.Data...)]. (if supported by the extension)
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
public final class JDACBuilder extends JDACBuilderData {

    JDACBuilder(@NotNull JDAContext context, @NotNull Class<?> baseClass, @NotNull String[] packages) {
        super(baseClass, packages, context);
    }

    /// @param classFinders the to be used [ClassFinder]s
    /// @apiNote This method overrides the underlying collection instead of adding to it.
    /// If you want to add own [ClassFinder]s while keeping the default reflective implementation, you have to add it explicitly via
    /// [ClassFinder#reflective(Class, String...)] too.
    @NotNull
    public JDACBuilder classFinders(@NotNull ClassFinder... classFinders) {
        this.classFinders = new ArrayList<>(Arrays.asList(classFinders));
        return this;
    }

    /// @param descriptor the [Descriptor] to be used
    @NotNull
    public JDACBuilder descriptor(@NotNull Descriptor descriptor) {
        this.descriptor = descriptor;
        return this;
    }

    /// @param localizationFunction The [LocalizationFunction] to use
    @NotNull
    public JDACBuilder localizationFunction(@NotNull LocalizationFunction localizationFunction) {
        this.localizationFunction = Objects.requireNonNull(localizationFunction);
        return this;
    }

    /// @param instanceProvider the implementation of [InteractionControllerInstantiator] to use
    public JDACBuilder instanceProvider(InteractionControllerInstantiator instanceProvider) {
        this.controllerInstantiator = instanceProvider;
        return this;
    }

    /// @param expirationStrategy The [ExpirationStrategy] to be used
    @NotNull
    public JDACBuilder expirationStrategy(@NotNull ExpirationStrategy expirationStrategy) {
        this.expirationStrategy = Objects.requireNonNull(expirationStrategy);
        return this;
    }

    /// @param priority   The [Priority] with what the [Middleware] should be registered
    /// @param middleware The to be registered [Middleware]
    @NotNull
    public JDACBuilder middleware(@NotNull Priority priority, @NotNull Middleware middleware) {
        Objects.requireNonNull(priority);
        Objects.requireNonNull(middleware);

        middlewares.add(Map.entry(priority, middleware));
        return this;
    }

    /// @param type    The type that the given [TypeAdapter] can handle
    /// @param adapter The [TypeAdapter] to be registered
    @NotNull
    public JDACBuilder adapter(@NotNull Class<?> type, @NotNull TypeAdapter<?> adapter) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(adapter);

        typeAdapters.put(type, adapter);
        return this;
    }

    /// @param annotation The annotation for which the given [Validator] should be called
    /// @param validator  The [Validator] to be registered
    @NotNull
    public JDACBuilder validator(@NotNull Class<? extends Annotation> annotation, @NotNull Validator validator) {
        Objects.requireNonNull(annotation);
        Objects.requireNonNull(validator);

        validators.put(annotation, validator);
        return this;
    }

    /// @param permissionsProvider The [PermissionsProvider] that should be used
    @NotNull
    public JDACBuilder permissionsProvider(@NotNull PermissionsProvider permissionsProvider) {
        this.permissionsProvider = Objects.requireNonNull(permissionsProvider);
        return this;
    }

    /// @param errorMessageFactory The [ErrorMessageFactory] that should be used
    @NotNull
    public JDACBuilder errorMessageFactory(@NotNull ErrorMessageFactory errorMessageFactory) {
        this.errorMessageFactory = Objects.requireNonNull(errorMessageFactory);
        return this;
    }

    /// @param guildScopeProvider The [GuildScopeProvider] that should be used
    @NotNull
    public JDACBuilder guildScopeProvider(@NotNull GuildScopeProvider guildScopeProvider) {
        this.guildScopeProvider = Objects.requireNonNull(guildScopeProvider);
        return this;
    }

    /// @param globalReplyConfig the [ReplyConfig] to be used as a global fallback option
    @NotNull
    public JDACBuilder globalReplyConfig(@NotNull ReplyConfig globalReplyConfig) {
        this.globalReplyConfig = globalReplyConfig;
        return this;
    }

    /// Registers [Extension.Data] that will be passed to the respective [Extension]s to configure them properly.
    ///
    /// @param data the instances of [Extension.Data] to be used
    @NotNull
    public JDACBuilder extensionData(@NotNull Extension.Data... data) {
        for (Extension.Data entity : data) {
            extensionData.put(entity.getClass(), entity);
        }
        return this;
    }

    /// Specifies a way to filter found implementations of [Extension] if you have clashing or cycling dependencies for example.
    ///
    /// @param strategy the filtering strategy to be used either [FilterStrategy#INCLUDE] or [FilterStrategy#EXCLUDE]
    /// @param classes  the classes to be filtered
    /// @apiNote This method compares the [`fully classified class name`][Class#getName()] of all [Extension] implementations by using [String#startsWith(String)],
    /// so it's possible to include/exclude a bunch of classes in the same package by just providing the package name.
    @NotNull
    public JDACBuilder filterExtensions(@NotNull FilterStrategy strategy, @NotNull String... classes) {
        this.extensionFilter = new ExtensionFilter(strategy, Arrays.asList(classes));
        return this;
    }

    /// This method applies all found implementations of [Extension],
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
                controllerInstantiator(),
                globalReplyConfig()
        );
        jdaCommands.start(mergedClassFinder(), baseClass(), packages());
        return jdaCommands;
    }

    /// The two available filter strategies
    public enum FilterStrategy {
        /// includes the defined classes
        INCLUDE,
        /// excludes the defined classes
        EXCLUDE
    }

    /// Will be thrown if anything goes wrong while configuring jda-commands.
    public static class ConfigurationException extends RuntimeException {
        public ConfigurationException(String message) {
            super("Error while trying to configure jda-commands: " + message);
        }
    }
}
