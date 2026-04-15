package io.github.kaktushose.jdac;

import io.github.kaktushose.jdac.definitions.description.ClassFinder;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition.CommandConfig;
import io.github.kaktushose.jdac.dispatching.adapter.AdapterType;
import io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter;
import io.github.kaktushose.jdac.dispatching.adapter.internal.TypeAdapters;
import io.github.kaktushose.jdac.dispatching.expiration.ExpirationStrategy;
import io.github.kaktushose.jdac.dispatching.instance.Instantiator;
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
import io.github.kaktushose.jdac.internal.logging.JDACLogger;
import io.github.kaktushose.jdac.message.emoji.EmojiResolver;
import io.github.kaktushose.jdac.message.emoji.EmojiSource;
import io.github.kaktushose.jdac.message.i18n.FluavaLocalizer;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.i18n.Localizer;
import io.github.kaktushose.jdac.message.i18n.internal.BundleFinder;
import io.github.kaktushose.jdac.message.i18n.internal.JDACLocalizationFunction;
import io.github.kaktushose.jdac.message.placeholder.PlaceholderResolver;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import io.github.kaktushose.jdac.message.resolver.Resolver;
import io.github.kaktushose.jdac.permissions.DefaultPermissionsProvider;
import io.github.kaktushose.jdac.permissions.PermissionsProvider;
import io.github.kaktushose.jdac.property.JDACIntrospection;
import io.github.kaktushose.jdac.property.JDACProperty;
import io.github.kaktushose.jdac.property.JDACPropertyProvider;
import io.github.kaktushose.jdac.property.JDACScope;
import io.github.kaktushose.jdac.property.extension.Extension;
import io.github.kaktushose.jdac.property.extension.ExtensionFilter;
import io.github.kaktushose.jdac.property.internal.JDACIntrospectionImpl;
import io.github.kaktushose.jdac.property.internal.extension.Extensions;
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

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.kaktushose.jdac.property.JDACProperty.*;
import static io.github.kaktushose.jdac.property.internal.JDACInternalProperties.*;

/// This builder is used to build instances of [JDACommands].
///
/// Please note that values that can be set have a default implementation.
/// These following implementations are based on reflections. If you want to avoid reflections, you have to provide your own implementations for:
///
/// - [#descriptor(Descriptor)]
/// - [#classFinders(ClassFinder...)]
/// - [#instantiator(Instantiator)]
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
/// which will _add to the default_ and loaded ones and
///
/// - [#classFinders(ClassFinder...)]
/// - [#emojiSource(EmojiSource...)]
///
/// which will _override the default_ but add to the loaded ones.
///
/// These implementations of [Extension] can be additionally configured by adding the according implementation of [Extension.Data]
/// by calling [#extensionData(Extension.Data...)]. (if supported by the extension)
///
/// If any exception while configuration/start of JDA-Commands is thrown, the JDA instance is shutdown per default.
/// This can be configured by settings [JDACBuilder#shutdownJDA(boolean)].
///
/// ## Example
/// ```java
/// JDACommands jdaCommands = JDACommands.builder(jda)
///     .middleware(Priority.NORMAL, new TestMiddleware())
///     .globalReplyConfig(new InteractionDefinition.ReplyConfig(false, false, true))
///     .classFinders(ClassFinder.reflective(Main.class), ClassFinders.explicit(ButtonInteraction.class))
///     .start();
/// ```
///
/// @see Extension
public class JDACBuilder {

    private static final Logger log = JDACLogger.getLogger(JDACBuilder.class);

    static {
        Proteus.global().register(Type.of(Class.class), Type.of(String.class), Mapper.uni((klass, _) -> MappingResult.lossless(klass.getName())));
    }

    private final JDACIntrospectionImpl.Builder properties = JDACIntrospectionImpl.create(JDACScope.CONFIGURATION);

    JDACBuilder(JDAContext jdaContext) {
        // defaults
        properties.addFallback(PACKAGES, _ -> List.of());
        properties.addFallback(EXTENSION_FILTER, _ -> new ExtensionFilter(ExtensionFilter.FilterStrategy.EXCLUDE, List.of()));

        properties.addFallback(EXPIRATION_STRATEGY, _ -> ExpirationStrategy.AFTER_15_MINUTES);
        properties.addFallback(GLOBAL_COMMAND_CONFIG, _ -> new CommandConfig());
        properties.addFallback(GLOBAL_REPLY_CONFIG, _ -> new ReplyConfig());
        properties.addFallback(SHUTDOWN_JDA, _ -> true);
        properties.addFallback(LOCALIZE_COMMANDS, _ -> true);
        properties.addFallback(LOCALIZER, _ -> FluavaLocalizer.create(Locale.ENGLISH));
        properties.addFallback(PERMISSION_PROVIDER, _ -> new DefaultPermissionsProvider());
        properties.addFallback(ERROR_MESSAGE_FACTORY, ctx -> new DefaultErrorMessageFactory(ctx.get(MESSAGE_RESOLVER)));
        properties.addFallback(GUILD_SCOPE_PROVIDER, _ -> new DefaultGuildScopeProvider());
        properties.addFallback(DESCRIPTOR, _ -> Descriptor.REFLECTIVE);

        properties.addFallback(EMOJI_SOURCES, _ -> List.of(EmojiSource.reflective()));
        properties.addFallback(CLASS_FINDER, ctx -> {
            String[] resources = ctx.get(PACKAGES).toArray(String[]::new);
            return List.of(ClassFinder.reflective(resources));
        });

        properties.addFallback(EMBED_CONFIG, _ -> _ -> { });

        // non settable/provided services
        properties.addFallback(EMBED_CONFIG_INTERNAL, ctx -> {
            Embeds.Configuration embedConfig = new Embeds.Configuration(ctx.get(MESSAGE_RESOLVER), ctx.get(EMBED_SOURCES));
            try {
                ctx.get(EMBED_CONFIG).accept(embedConfig);
            } catch (Exception e) {
                if (ctx.get(SHUTDOWN_JDA)) {
                    ctx.get(JDA_CONTEXT).shutdown();
                }
                throw e;
            }

            return embedConfig;
        });
        properties.addFallback(EMBEDS, ctx -> ctx.get(EMBED_CONFIG_INTERNAL).build());

        properties.addFallback(BUNDLE_FINDER, ctx -> new BundleFinder(ctx.get(DESCRIPTOR)));
        properties.addFallback(I18N, ctx -> new I18n(ctx.get(BUNDLE_FINDER), ctx.get(LOCALIZER)));

        properties.addFallback(STRING_RESOLVER, ctx -> List.of(ctx.get(I18N), ctx.get(EMOJI_RESOLVER), ctx.get(PLACEHOLDER_RESOLVER)));
        properties.addFallback(MESSAGE_RESOLVER, ctx -> new MessageResolver(ctx.get(STRING_RESOLVER)));
        properties.addFallback(PLACEHOLDER_RESOLVER, _ -> new PlaceholderResolver());

        properties.addFallback(EMOJI_RESOLVER, ctx -> {
            Helpers.registerAppEmojis(ctx.get(JDA_CONTEXT), ctx.get(EMOJI_SOURCES));

            List<ApplicationEmoji> applicationEmojis = ctx.get(JDA_CONTEXT).applicationEmojis();
            return new EmojiResolver(applicationEmojis);
        });

        properties.addFallback(MERGED_CLASS_FINDER, ctx -> annotationClass -> ctx.get(CLASS_FINDER)
                .stream()
                .map(classFinder -> classFinder.search(annotationClass))
                .flatMap(Collection::stream)
                .toList());

        // must be set
        properties.addFallback(JDA_CONTEXT, _ -> jdaContext);
    }

    private <T> JDACBuilder addBuilderProperty(JDACProperty<T> type, Function<JDACIntrospection, T> supplier) {
        properties.addBuilder(type, supplier);
        return this;
    }

    /// Some default implementations like [ClassFinder#reflective(String...)] will scan the classpath to find classes
    /// and resource. This config option allows you to restrict the to be scanned packages.
    ///
    /// @param packages the packages to scan recursively
    public JDACBuilder packages(String... packages) {
        return addBuilderProperty(PACKAGES, _ -> List.of(packages));
    }

    /// @param classFinders the to be used [ClassFinder]s
    /// @apiNote This method overrides the default/fallback value but adds to the loaded ones.
    /// If you want to add own [ClassFinder]s while keeping the default reflective implementation, you have to add it explicitly via
    /// [ClassFinder#reflective(String...)] too.
    public JDACBuilder classFinders(ClassFinder... classFinders) {
        return addBuilderProperty(CLASS_FINDER, _ -> List.of(classFinders));
    }

    /// Application emojis loaded from [EmojiSource]s will be registered upon startup with help of
    /// [JDA#createApplicationEmoji(String, Icon)].
    ///
    /// @param sources the to be used [EmojiSource]s
    /// @apiNote This method overrides the default/fallback value but adds to the loaded ones.
    /// If you want to add own [EmojiSource]s while keeping the default reflective implementation, you have to add it explicitly via
    /// [EmojiSource#reflective(String...)] too.
    public JDACBuilder emojiSource(EmojiSource... sources) {
        return addBuilderProperty(EMOJI_SOURCES, _ -> List.of(sources));
    }

    /// @param descriptor the [Descriptor] to be used
    public JDACBuilder descriptor(Descriptor descriptor) {
        return addBuilderProperty(DESCRIPTOR, _ -> descriptor);
    }

    /// Configuration step for the Embed API of JDA-Commands.
    ///
    /// Use the given [EmbedConfig] to declare placeholders or data sources.
    public JDACBuilder embeds(Consumer<EmbedConfig> consumer) {
        return addBuilderProperty(EMBED_CONFIG, _ -> consumer);
    }

    /// @param localizer The [Localizer] to use
    public JDACBuilder localizer(Localizer localizer) {
        return addBuilderProperty(LOCALIZER, _ -> localizer);
    }

    /// @param instantiator the implementation of [Instantiator] to use
    public JDACBuilder instantiator(Instantiator instantiator) {
        return addBuilderProperty(INSTANTIATOR, _ -> instantiator);
    }

    /// @param strategy The [ExpirationStrategy] to be used
    public JDACBuilder expirationStrategy(ExpirationStrategy strategy) {
        return addBuilderProperty(EXPIRATION_STRATEGY, _ -> strategy);
    }

    /// @param priority   The [Priority] with what the [Middleware] should be registered
    /// @param middleware The to be registered [Middleware]
    public JDACBuilder middleware(Priority priority, Middleware middleware) {
        return addBuilderProperty(MIDDLEWARE, _ -> List.of(Map.entry(priority, middleware)));
    }

    /// @param source  The source type that the given [TypeAdapter] can handle
    /// @param target  The target type that the given [TypeAdapter] can handle
    /// @param adapter The [TypeAdapter] to be registered
    public <S, T> JDACBuilder adapter(Class<S> source, Class<T> target, TypeAdapter<S, T> adapter) {
        return addBuilderProperty(TYPE_ADAPTER, _ -> Map.of(new AdapterType<>(Type.of(source), Type.of(target)), adapter));
    }

    /// @param annotation The annotation for which the given [Validator] should be called
    /// @param validator  The [Validator] to be registered
    public JDACBuilder validator(Class<? extends Annotation> annotation, Validator<?, ?> validator) {
        return addBuilderProperty(VALIDATOR, _ -> Map.of(annotation, validator));
    }

    /// @param permissionsProvider The [PermissionsProvider] that should be used
    public JDACBuilder permissionsProvider(PermissionsProvider permissionsProvider) {
        return addBuilderProperty(PERMISSION_PROVIDER, _ -> permissionsProvider);
    }

    /// @param errorMessageFactory The [ErrorMessageFactory] that should be used
    public JDACBuilder errorMessageFactory(ErrorMessageFactory errorMessageFactory) {
        return addBuilderProperty(ERROR_MESSAGE_FACTORY, _ -> errorMessageFactory);
    }

    /// @param guildScopeProvider The [GuildScopeProvider] that should be used
    public JDACBuilder guildScopeProvider(GuildScopeProvider guildScopeProvider) {
        return addBuilderProperty(GUILD_SCOPE_PROVIDER, _ -> guildScopeProvider);
    }


    /// @param globalReplyConfig the [ReplyConfig] to be used as a global fallback option
    public JDACBuilder globalReplyConfig(ReplyConfig globalReplyConfig) {
        return addBuilderProperty(GLOBAL_REPLY_CONFIG, _ -> globalReplyConfig);
    }

    /// @param config the [CommandConfig] to be used as a global fallback option
    public JDACBuilder globalCommandConfig(CommandConfig config) {
        return addBuilderProperty(GLOBAL_COMMAND_CONFIG, _ -> config);
    }

    /// Registers [Extension.Data] that will be passed to the respective [Extension]s to configure them properly.
    ///
    /// @param data the instances of [Extension.Data] to be used
    public JDACBuilder extensionData(Extension.Data... data) {
        return addBuilderProperty(EXTENSION_DATA, _ -> Arrays.stream(data)
                .collect(Collectors.toMap(Extension.Data::getClass, Function.identity())));
    }


    /// Whether the JDA instance should be shutdown if the configuration/start of JDA-Commands fails or if
    /// [JDACommands#shutdown()] is called
    ///
    /// @param shutdown whether to shut down the JDA instance, default true
    public JDACBuilder shutdownJDA(boolean shutdown) {
        return addBuilderProperty(SHUTDOWN_JDA, _ -> shutdown);
    }

    /// Whether JDA-Commands should use the [I18n] feature to localize commands.
    ///
    /// @param localize whether to localize commands, default true
    /// @see LocalizationFunction
    /// @see FluavaLocalizer FluavaLocalizer
    public JDACBuilder localizeCommands(boolean localize) {
        return addBuilderProperty(LOCALIZE_COMMANDS, _ -> localize);
    }


    /// Specifies a way to filter found implementations of [Extension] if you have clashing or cycling dependencies for example.
    ///
    /// @param strategy the filtering strategy to be used either [ExtensionFilter.FilterStrategy#INCLUDE] or [ExtensionFilter.FilterStrategy#EXCLUDE]
    /// @param classes  the classes to be filtered
    /// @apiNote This method compares the [`fully classified class name`][Class#getName()] of all [Extension] implementations by using [String#startsWith(String)],
    /// so it's possible to include/exclude a bunch of extensions sharing the same base package by just providing the package name.
    /// @see ExtensionFilter
    public JDACBuilder filterExtensions(ExtensionFilter.FilterStrategy strategy, String... classes) {
        return addBuilderProperty(EXTENSION_FILTER, _ -> new ExtensionFilter(strategy, Arrays.asList(classes)));
    }

    /// Adds a [Resolver] in the resolution chain for strings provided by the user in annotations, components etc.
    ///
    /// All [Resolver]s are accumulated by [MessageResolver].
    /// This method adds to the default resolvers like localization, placeholders and emojis
    ///
    /// @param resolvers the [Resolver]s to add
    /// @see MessageResolver
    /// @see Resolver
    @SafeVarargs
    public final JDACBuilder stringResolver(Resolver<String>... resolvers) {
        addBuilderProperty(STRING_RESOLVER, _ -> Arrays.asList(resolvers));
        return this;
    }

    /// Allows the dynamic configuration of JDA-Commands by setting [JDACProperty]s directly.
    ///
    /// @param property the [JDACProperty] to be set
    /// @param supplier the function providing the custom value for it ([JDACPropertyProvider#supplier()])
    /// @implNote the values will be added as [JDACPropertyProvider]s with priority set to [Integer#MAX_VALUE]
    /// like they were set by any other method of this class.
    /// @see JDACProperty
    /// @see JDACPropertyProvider#supplier()
    public <T> JDACBuilder setProperty(JDACProperty<T> property, Function<JDACIntrospection, T> supplier) {
        return addBuilderProperty(property, supplier);
    }

    /// This method applies all found implementations of [Extension],
    /// instantiates an instance of [JDACommands] and starts the framework.
    public JDACommands start() {
        log.info("Starting JDA-Commands...");

        Extensions extensions = loadExtensions();

        JDACIntrospectionImpl introspection = properties.build();

        return ScopedValue.where(JDACIntrospectionImpl.INTROSPECTION, introspection).call(() -> {
            try {
                Middlewares middlewares = new Middlewares(introspection.get(MIDDLEWARE), introspection.get(ERROR_MESSAGE_FACTORY), introspection.get(PERMISSION_PROVIDER));
                TypeAdapters typeAdapters = new TypeAdapters(introspection.get(TYPE_ADAPTER), introspection.get(MESSAGE_RESOLVER));

                JDACIntrospectionImpl.Builder initBuilder = introspection.createChild(JDACScope.INITIALIZED)
                        .addFallback(INTERACTION_REGISTRY, ctx -> new InteractionRegistry(
                                new Validators(ctx.get(VALIDATOR)),
                                ctx.get(MESSAGE_RESOLVER),
                                ctx.get(DESCRIPTOR)
                        ))
                        .addFallback(DEFINITIONS, ctx -> ctx.get(INTERACTION_REGISTRY))
                        .addFallback(MIDDLEWARES, _ -> middlewares)
                        .addFallback(TYPE_ADAPTERS, _ -> typeAdapters)
                        .addFallback(LOCALIZATION_FUNCTION, JDACLocalizationFunction.PROVIDER_FUNC);

                JDACommands jdaCommands = new JDACommands(initBuilder);
                jdaCommands.start(extensions);

                return jdaCommands;
            } catch (JDACException e) {
                if (introspection.get(SHUTDOWN_JDA)) {
                    introspection.get(JDA_CONTEXT).shutdown();
                }
                throw e;
            }
        });
    }

    private Extensions loadExtensions() {
        Extensions extensions = new Extensions();
        extensions.load(properties.build()); // Introspection just for user settable types
        extensions.register(properties);

        return extensions;
    }

}
