package io.github.kaktushose.jdac;

import dev.goldmensch.fluava.Fluava;
import io.github.kaktushose.jdac.configuration.Extension;
import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.PropertyProvider;
import io.github.kaktushose.jdac.configuration.internal.ExtensionFilter;
import io.github.kaktushose.jdac.configuration.internal.Properties;
import io.github.kaktushose.jdac.configuration.internal.Resolver;
import io.github.kaktushose.jdac.definitions.description.ClassFinder;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition.CommandConfig;
import io.github.kaktushose.jdac.dispatching.FrameworkContext;
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
import io.github.kaktushose.jdac.internal.Helpers;
import io.github.kaktushose.jdac.internal.JDAContext;
import io.github.kaktushose.jdac.message.MessageResolver;
import io.github.kaktushose.jdac.message.emoji.EmojiResolver;
import io.github.kaktushose.jdac.message.emoji.EmojiSource;
import io.github.kaktushose.jdac.message.i18n.FluavaLocalizer;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.i18n.Localizer;
import io.github.kaktushose.jdac.permissions.DefaultPermissionsProvider;
import io.github.kaktushose.jdac.permissions.PermissionsProvider;
import io.github.kaktushose.jdac.scope.DefaultGuildScopeProvider;
import io.github.kaktushose.jdac.scope.GuildScopeProvider;
import io.github.kaktushose.proteus.Proteus;
import io.github.kaktushose.proteus.mapping.Mapper;
import io.github.kaktushose.proteus.mapping.MappingResult;
import io.github.kaktushose.proteus.type.Type;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.emoji.ApplicationEmoji;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.kaktushose.jdac.configuration.Property.*;
import static io.github.kaktushose.jdac.configuration.internal.InternalPropertyProviders.*;

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
/// If any exception while configuration/start of JDA-Commands is thrown, the JDA instance is shutdown per default.
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
public class JDACBuilder {

    static {
        Proteus.global().register(Type.of(Class.class), Type.of(String.class), Mapper.uni((klass, _) -> MappingResult.lossless(klass.getName())));
    }

    private static final Logger log = LoggerFactory.getLogger(JDACBuilder.class);

    private final Properties properties = new Properties();

    JDACBuilder(JDAContext jdaContext) {
        // defaults
        addFallback(PACKAGES, _ -> List.of());
        addFallback(EXTENSION_FILTER, _ -> new ExtensionFilter(FilterStrategy.EXCLUDE, List.of()));

        addFallback(EXPIRATION_STRATEGY, _ -> ExpirationStrategy.AFTER_15_MINUTES);
        addFallback(GLOBAL_COMMAND_CONFIG, _ -> new CommandConfig());
        addFallback(GLOBAL_REPLY_CONFIG, _ -> new ReplyConfig());
        addFallback(SHUTDOWN_JDA, _ -> true);
        addFallback(LOCALIZE_COMMANDS, _ -> true);
        addFallback(LOCALIZER, _ -> new FluavaLocalizer(Fluava.create(Locale.ENGLISH)));
        addFallback(PERMISSION_PROVIDER, _ -> new DefaultPermissionsProvider());
        addFallback(ERROR_MESSAGE_FACTORY, ctx -> new DefaultErrorMessageFactory(ctx.get(EMBED_CONFIG).buildError()));
        addFallback(GUILD_SCOPE_PROVIDER, _ -> new DefaultGuildScopeProvider());
        addFallback(DESCRIPTOR, _ -> Descriptor.REFLECTIVE);

        addFallback(EMOJI_SOURCES, _ -> List.of(EmojiSource.reflective()));
        addFallback(CLASS_FINDER, ctx -> {
            String[] resources = ctx.get(PACKAGES).toArray(String[]::new);
            return List.of(ClassFinder.reflective(resources));
        });
        addFallback(EMBED_CONFIG, ctx -> new Embeds.Configuration(ctx.get(MESSAGE_RESOLVER)));

        // non settable/provided services
        addFallback(EMBEDS, ctx -> ctx.get(EMBED_CONFIG).buildDefault());

        addFallback(I18N, ctx -> new I18n(ctx.get(DESCRIPTOR), ctx.get(LOCALIZER)));
        addFallback(MESSAGE_RESOLVER,
                ctx -> new MessageResolver(ctx.get(I18N), ctx.get(EMOJI_RESOLVER)));

        addFallback(EMOJI_RESOLVER, ctx -> {
            Helpers.registerAppEmojis(ctx.get(JDA_CONTEXT), ctx.get(EMOJI_SOURCES));

            List<ApplicationEmoji> applicationEmojis = ctx.get(JDA_CONTEXT).applicationEmojis();
            return new EmojiResolver(applicationEmojis);
        });

        addFallback(MERGED_CLASS_FINDER, ctx -> annotationClass -> ctx.get(CLASS_FINDER)
                .stream()
                .map(classFinder -> classFinder.search(annotationClass))
                .flatMap(Collection::stream)
                .toList());

        // must be set
        addFallback(JDA_CONTEXT, _ -> jdaContext);
    }

    private <T> void addFallback(Property<T> type, Function<PropertyProvider.Context, T> supplier) {
        ScopedValue.where(Properties.INSIDE_FRAMEWORK, true).run(() -> properties.add(PropertyProvider.create(type, Properties.FALLBACK_PRIORITY, supplier)));
    }

    private <T> JDACBuilder addUserProperty(Property<T> type, Function<PropertyProvider.Context, T> supplier) {
        ScopedValue.where(Properties.INSIDE_FRAMEWORK, true).run(() -> properties.add(PropertyProvider.create(type, Properties.USER_PRIORITY, supplier)));
        return this;
    }

    /// Some default implementations like [ClassFinder#reflective(String...)] will scan the classpath to find classes
    /// and resource. This config option allows you to restrict the to be scanned packages.
    ///
    /// @param packages the packages to scan recursively
    public JDACBuilder packages(String... packages) {
        return addUserProperty(PACKAGES, _ -> List.of(packages));
    }

    /// @param classFinders the to be used [ClassFinder]s
    ///
    /// @apiNote This method overrides the underlying collection instead of adding to it.
    /// If you want to add own [ClassFinder]s while keeping the default reflective implementation, you have to add it explicitly via
    /// [ClassFinder#reflective(String...)] too.
    public JDACBuilder classFinders(ClassFinder... classFinders) {
        return addUserProperty(CLASS_FINDER, _ -> List.of(classFinders));
    }

    /// Application emojis loaded from [EmojiSource]s will be registered upon startup with help of
    /// [JDA#createApplicationEmoji(String, Icon)].
    ///
    /// @param sources the to be used [EmojiSource]s
    /// @apiNote This method overrides the underlying collection instead of adding to it.
    /// If you want to add own [EmojiSource]s while keeping the default reflective implementation, you have to add it explicitly via
    /// [EmojiSource#reflective(String...)] too.
    public JDACBuilder emojiSource(EmojiSource... sources) {
        return addUserProperty(EMOJI_SOURCES, _ -> List.of(sources));
    }

    /// @param descriptor the [Descriptor] to be used
    public JDACBuilder descriptor(Descriptor descriptor) {
        return addUserProperty(DESCRIPTOR, _ -> descriptor);
    }

    /// Configuration step for the Embed API of JDA-Commands.
    ///
    /// Use the given [EmbedConfig] to declare placeholders or data sources.
    public JDACBuilder embeds(Consumer<EmbedConfig> consumer) {
        return addUserProperty(EMBED_CONFIG, ctx -> {
            Embeds.Configuration embedConfig = new Embeds.Configuration(ctx.get(MESSAGE_RESOLVER));
            try {
                consumer.accept(embedConfig);
            } catch (Exception e) {
                if (ctx.get(SHUTDOWN_JDA)) ctx.get(JDA_CONTEXT).shutdown();
                throw e;
            }

            return embedConfig;
        });
    }

    /// @param localizer The [Localizer] to use
    public JDACBuilder localizer(Localizer localizer) {
        return addUserProperty(LOCALIZER, _ -> localizer);
    }

    /// @param instanceProvider the implementation of [InteractionControllerInstantiator] to use
    public JDACBuilder instanceProvider(InteractionControllerInstantiator instanceProvider) {
        return addUserProperty(INTERACTION_CONTROLLER_INSTANTIATOR, _ -> instanceProvider);
    }

    /// @param strategy The [ExpirationStrategy] to be used
    public JDACBuilder expirationStrategy(ExpirationStrategy strategy) {
        return addUserProperty(EXPIRATION_STRATEGY, _ -> strategy);
    }

    /// @param priority   The [Priority] with what the [Middleware] should be registered
    /// @param middleware The to be registered [Middleware]
    public JDACBuilder middleware(Priority priority, Middleware middleware) {
        return addUserProperty(MIDDLEWARE, _ -> List.of(Map.entry(priority, middleware)));
    }

    /// @param source  The source type that the given [TypeAdapter] can handle
    /// @param target  The target type that the given [TypeAdapter] can handle
    /// @param adapter The [TypeAdapter] to be registered
    public JDACBuilder adapter(Class<?> source, Class<?> target, TypeAdapter<?, ?> adapter) {
        return addUserProperty(TYPE_ADAPTER, _ -> Map.of(Map.entry(Type.of(source), Type.of(target)), adapter));
    }

    /// @param annotation The annotation for which the given [Validator] should be called
    /// @param validator  The [Validator] to be registered
    public JDACBuilder validator(Class<? extends Annotation> annotation, Validator<?, ?> validator) {
        return addUserProperty(VALIDATOR, _ -> Map.of(annotation, validator));
    }

    /// @param permissionsProvider The [PermissionsProvider] that should be used
    public JDACBuilder permissionsProvider(PermissionsProvider permissionsProvider) {
        return addUserProperty(PERMISSION_PROVIDER, _ -> permissionsProvider);
    }

    /// @param errorMessageFactory The [ErrorMessageFactory] that should be used
    public JDACBuilder errorMessageFactory(ErrorMessageFactory errorMessageFactory) {
        return addUserProperty(ERROR_MESSAGE_FACTORY, _ -> errorMessageFactory);
    }

    /// @param guildScopeProvider The [GuildScopeProvider] that should be used
    public JDACBuilder guildScopeProvider(GuildScopeProvider guildScopeProvider) {
        return addUserProperty(GUILD_SCOPE_PROVIDER, _ -> guildScopeProvider);
    }


    /// @param globalReplyConfig the [ReplyConfig] to be used as a global fallback option
    public JDACBuilder globalReplyConfig(ReplyConfig globalReplyConfig) {
        return addUserProperty(GLOBAL_REPLY_CONFIG, _ -> globalReplyConfig);
    }

    /// @param config the [CommandConfig] to be used as a global fallback option
    public JDACBuilder globalCommandConfig(CommandConfig config) {
        return addUserProperty(GLOBAL_COMMAND_CONFIG, _ -> config);
    }

    /// Registers [Extension.Data] that will be passed to the respective [Extension]s to configure them properly.
    ///
    /// @param data the instances of [Extension.Data] to be used
    public JDACBuilder extensionData(Extension.Data... data) {
        return addUserProperty(EXTENSION_DATA, _ -> Arrays.stream(data)
                .collect(Collectors.toMap(Extension.Data::getClass, Function.identity())));
    }


    /// Whether the JDA instance should be shutdown if the configuration/start of JDA-Commands fails or if
    /// [JDACommands#shutdown()] is called
    ///
    /// @param shutdown whether to shut down the JDA instance, default true
    public JDACBuilder shutdownJDA(boolean shutdown) {
        return addUserProperty(SHUTDOWN_JDA, _ -> shutdown);
    }

    /// Whether JDA-Commands should use the [I18n] feature to localize commands.
    ///
    /// @param localize whether to localize commands, default true
    /// @see LocalizationFunction
    /// @see FluavaLocalizer FluavaLocalizer
    public JDACBuilder localizeCommands(boolean localize) {
        return addUserProperty(LOCALIZE_COMMANDS, _ -> localize);
    }


    /// Specifies a way to filter found implementations of [Extension] if you have clashing or cycling dependencies for example.
    ///
    /// @param strategy the filtering strategy to be used either [FilterStrategy#INCLUDE] or [FilterStrategy#EXCLUDE]
    /// @param classes  the classes to be filtered
    /// @apiNote This method compares the [`fully classified class name`][Class#getName()] of all [Extension] implementations by using [String#startsWith(String)],
    /// so it's possible to include/exclude a bunch of extensions sharing the same base package by just providing the package name.
    public JDACBuilder filterExtensions(FilterStrategy strategy, String... classes) {
        return addUserProperty(EXTENSION_FILTER, _ -> new ExtensionFilter(strategy, Arrays.asList(classes)));
    }

    /// This method applies all found implementations of [Extension],
    /// instantiates an instance of [JDACommands] and starts the framework.
    public JDACommands start() {
        Resolver loader = properties.createResolver();

        try {
            log.info("Starting JDA-Commands...");

            FrameworkContext frameworkContext = new FrameworkContext(
                    new Middlewares(loader.get(MIDDLEWARE), loader.get(ERROR_MESSAGE_FACTORY), loader.get(PERMISSION_PROVIDER)),
                    loader.get(ERROR_MESSAGE_FACTORY),
                    new InteractionRegistry(
                            new Validators(loader.get(VALIDATOR)),
                            loader.get(I18N),
                            loader.get(LOCALIZE_COMMANDS) ? loader.get(I18N).localizationFunction() : (_) -> Map.of(),
                            loader.get(DESCRIPTOR)
                    ),
                    new TypeAdapters(loader.get(TYPE_ADAPTER), loader.get(I18N)),
                    loader.get(EXPIRATION_STRATEGY),
                    loader.get(INTERACTION_CONTROLLER_INSTANTIATOR),
                    loader.get(EMBEDS),
                    loader.get(I18N),
                    loader.get(MESSAGE_RESOLVER),
                    loader.get(GLOBAL_REPLY_CONFIG),
                    loader.get(GLOBAL_COMMAND_CONFIG)
            );

            JDACommands jdaCommands = new JDACommands(
                    frameworkContext,
                    loader.get(JDA_CONTEXT),
                    loader.get(GUILD_SCOPE_PROVIDER),
                    loader.get(SHUTDOWN_JDA)
            );

            jdaCommands.start(loader.get(MERGED_CLASS_FINDER));
            return jdaCommands;
        } catch (JDACException e) {
            if (loader.get(SHUTDOWN_JDA)) {
                loader.get(JDA_CONTEXT).shutdown();
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
