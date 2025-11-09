package io.github.kaktushose.jdac;

import io.github.kaktushose.jdac.definitions.description.ClassFinder;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition.CommandConfig;
import io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter;
import io.github.kaktushose.jdac.dispatching.adapter.internal.TypeAdapters;
import io.github.kaktushose.jdac.dispatching.expiration.ExpirationStrategy;
import io.github.kaktushose.jdac.dispatching.instance.InteractionControllerInstantiator;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import io.github.kaktushose.jdac.dispatching.middleware.Priority;
import io.github.kaktushose.jdac.dispatching.middleware.internal.Middlewares;
import io.github.kaktushose.jdac.dispatching.validation.Validator;
import io.github.kaktushose.jdac.dispatching.validation.internal.Validators;
import io.github.kaktushose.jdac.embeds.EmbedConfig;
import io.github.kaktushose.jdac.embeds.error.DefaultErrorMessageFactory;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import io.github.kaktushose.jdac.extension.Extension;
import io.github.kaktushose.jdac.extension.JDACBuilderData;
import io.github.kaktushose.jdac.extension.internal.ExtensionFilter;
import io.github.kaktushose.jdac.message.MessageResolver;
import io.github.kaktushose.jdac.message.emoji.EmojiSource;
import io.github.kaktushose.jdac.message.i18n.FluavaLocalizer;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.i18n.Localizer;
import io.github.kaktushose.jdac.permissions.PermissionsProvider;
import io.github.kaktushose.jdac.scope.GuildScopeProvider;
import io.github.kaktushose.proteus.type.Type;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
/// - [#adapter(Class, Class, TypeAdapter)]
/// - [#validator(Class, Validator)]
///
/// which will add to the default and loaded ones.
///
/// These implementations of [Extension] can be additionally configured by adding the according implementation of [Extension.Data]
/// by calling [#extensionData(Extension.Data...)]. (if supported by the extension)
///
/// If any exception while configuration/start of JDA-Commands is thrown, the JDA instance if shutdown per default.
/// This can be configured by settings [JDACBuilder#shutdownJDA(boolean)].
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

    private BiConsumer<MessageResolver, ErrorMessageFactory> configureEmbeds = (_, _) -> {};
    private Embeds.@Nullable Configuration embedConfig;

    JDACBuilder(JDAContext context, Class<?> baseClass, String[] packages) {
        super(baseClass, packages, context);
    }

    /// @param classFinders the to be used [ClassFinder]s
    /// @apiNote This method overrides the underlying collection instead of adding to it.
    /// If you want to add own [ClassFinder]s while keeping the default reflective implementation, you have to add it explicitly via
    /// [ClassFinder#reflective(Class, String...)] too.
    public JDACBuilder classFinders(ClassFinder... classFinders) {
        this.classFinders = new ArrayList<>(Arrays.asList(classFinders));
        return this;
    }

    /// Application emojis loaded from [EmojiSource]s will be registered upon startup with help of
    /// [JDA#createApplicationEmoji(String, Icon)].
    ///
    /// @param sources the to be used [EmojiSource]s
    /// @apiNote This method overrides the underlying collection instead of adding to it.
    /// If you want to add own [EmojiSource]s while keeping the default reflective implementation, you have to add it explicitly via
    /// [EmojiSource#reflective(Class, String...)] too.
    public JDACBuilder emojiSources(EmojiSource... sources) {
        this.emojiSources = new ArrayList<>(Arrays.asList(sources));
        return this;
    }

    /// @param descriptor the [Descriptor] to be used
    public JDACBuilder descriptor(Descriptor descriptor) {
        this.descriptor = descriptor;
        return this;
    }

    /// Configuration step for the Embed API of JDA-Commands.
    ///
    /// Use the given [EmbedConfig] to declare placeholders or data sources.
    public JDACBuilder embeds(Consumer<EmbedConfig> consumer) {
        configureEmbeds = (i18n, errorMessageFactory) -> {
            embedConfig = new Embeds.Configuration(i18n);
            try {
                consumer.accept(embedConfig);
            } catch (Exception e) {
                if (shutdownJDA()) context.shutdown();
                throw e;
            }

            this.embeds = embedConfig.buildDefault();
            if (errorMessageFactory instanceof DefaultErrorMessageFactory) {
                errorMessageFactory(new DefaultErrorMessageFactory(embedConfig.buildError()));
            }
        };
        return this;
    }

    /// @param localizer The [Localizer] to use
    public JDACBuilder localizer(Localizer localizer) {
        this.localizer = Objects.requireNonNull(localizer);
        return this;
    }

    /// @param instanceProvider the implementation of [InteractionControllerInstantiator] to use
    public JDACBuilder instanceProvider(InteractionControllerInstantiator instanceProvider) {
        this.controllerInstantiator = instanceProvider;
        return this;
    }

    /// @param expirationStrategy The [ExpirationStrategy] to be used
    public JDACBuilder expirationStrategy(ExpirationStrategy expirationStrategy) {
        this.expirationStrategy = Objects.requireNonNull(expirationStrategy);
        return this;
    }

    /// @param priority   The [Priority] with what the [Middleware] should be registered
    /// @param middleware The to be registered [Middleware]
    public JDACBuilder middleware(Priority priority, Middleware middleware) {
        Objects.requireNonNull(priority);
        Objects.requireNonNull(middleware);

        middlewares.add(Map.entry(priority, middleware));
        return this;
    }

    /// @param source  The source type that the given [TypeAdapter] can handle
    /// @param target  The target type that the given [TypeAdapter] can handle
    /// @param adapter The [TypeAdapter] to be registered
    public JDACBuilder adapter(Class<?> source, Class<?> target, TypeAdapter<?, ?> adapter) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        Objects.requireNonNull(adapter);

        typeAdapters.put(Map.entry(Type.of(source), Type.of(target)), adapter);
        return this;
    }

    /// @param annotation The annotation for which the given [Validator] should be called
    /// @param validator  The [Validator] to be registered
    public JDACBuilder validator(Class<? extends Annotation> annotation, Validator<?, ?> validator) {
        Objects.requireNonNull(annotation);
        Objects.requireNonNull(validator);

        validators.put(annotation, validator);
        return this;
    }

    /// @param permissionsProvider The [PermissionsProvider] that should be used
    public JDACBuilder permissionsProvider(PermissionsProvider permissionsProvider) {
        this.permissionsProvider = Objects.requireNonNull(permissionsProvider);
        return this;
    }

    /// @param errorMessageFactory The [ErrorMessageFactory] that should be used
    public JDACBuilder errorMessageFactory(ErrorMessageFactory errorMessageFactory) {
        this.errorMessageFactory = Objects.requireNonNull(errorMessageFactory);
        return this;
    }

    /// @param guildScopeProvider The [GuildScopeProvider] that should be used
    public JDACBuilder guildScopeProvider(GuildScopeProvider guildScopeProvider) {
        this.guildScopeProvider = Objects.requireNonNull(guildScopeProvider);
        return this;
    }

    /// @param globalReplyConfig the [ReplyConfig] to be used as a global fallback option
    public JDACBuilder globalReplyConfig(ReplyConfig globalReplyConfig) {
        this.globalReplyConfig = globalReplyConfig;
        return this;
    }

    /// @param config the [CommandConfig] to be used as a global fallback option
    public JDACBuilder globalCommandConfig(CommandConfig config) {
        this.globalCommandConfig = config;
        return this;
    }

    /// Registers [Extension.Data] that will be passed to the respective [Extension]s to configure them properly.
    ///
    /// @param data the instances of [Extension.Data] to be used
    public JDACBuilder extensionData(Extension.Data... data) {
        for (Extension.Data entity : data) {
            extensionData.put(entity.getClass(), entity);
        }
        return this;
    }

    /// Whether the JDA instance should be shutdown if the configuration/start of JDA-Commands fails or if
    /// [JDACommands#shutdown()] is called
    ///
    /// @param shutdown whether to shut down the JDA instance, default true
    public JDACBuilder shutdownJDA(boolean shutdown) {
        shutdownJDA = shutdown;
        return this;
    }

    /// Whether JDA-Commands should use the [I18n] feature to localize commands.
    ///
    /// @param localize whether to localize commands, default true
    /// @see LocalizationFunction
    /// @see FluavaLocalizer FluavaLocalizer
    public JDACBuilder localizeCommands(boolean localize) {
        localizeCommands = localize;
        return this;
    }

    /// Specifies a way to filter found implementations of [Extension] if you have clashing or cycling dependencies for example.
    ///
    /// @param strategy the filtering strategy to be used either [FilterStrategy#INCLUDE] or [FilterStrategy#EXCLUDE]
    /// @param classes  the classes to be filtered
    /// @apiNote This method compares the [`fully classified class name`][Class#getName()] of all [Extension] implementations by using [String#startsWith(String)],
    /// so it's possible to include/exclude a bunch of classes in the same package by just providing the package name.
    public JDACBuilder filterExtensions(FilterStrategy strategy, String... classes) {
        this.extensionFilter = new ExtensionFilter(strategy, Arrays.asList(classes));
        return this;
    }

    /// This method applies all found implementations of [Extension],
    /// instantiates an instance of [JDACommands] and starts the framework.
    public JDACommands start() {
        try {
            log.info("Starting JDA-Commands...");
            // this order matters!
            I18n i18n = i18n();
            MessageResolver messageResolver = messageResolver();
            configureEmbeds.accept(messageResolver, errorMessageFactory());
            JDACommands jdaCommands = new JDACommands(
                    context(),
                    expirationStrategy(),
                    new TypeAdapters(typeAdapters(), i18n),
                    new Middlewares(middlewares(), errorMessageFactory(), permissionsProvider()),
                    errorMessageFactory(),
                    guildScopeProvider(),
                    new InteractionRegistry(
                            new Validators(validators()),
                            i18n,
                            localizeCommands() ? i18n.localizationFunction() : (_) -> Map.of(),
                            descriptor()
                    ),
                    controllerInstantiator(),
                    globalReplyConfig(),
                    globalCommandConfig(),
                    i18n,
                    messageResolver,
                    embeds(messageResolver),
                    shutdownJDA()
            );
            jdaCommands.start(mergedClassFinder());
            return jdaCommands;
        } catch (JDACException e) {
            if (shutdownJDA()) {
                context().shutdown();
            }
            throw e;
        }
    }

    /// The two available filter strategies
    public enum FilterStrategy {
        /// includes the defined classes
        INCLUDE,
        /// excludes the defined classes
        EXCLUDE
    }

}
