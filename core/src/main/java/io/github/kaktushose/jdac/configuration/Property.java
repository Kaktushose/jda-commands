package io.github.kaktushose.jdac.configuration;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.definitions.description.ClassFinder;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition;
import io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter;
import io.github.kaktushose.jdac.dispatching.expiration.ExpirationStrategy;
import io.github.kaktushose.jdac.dispatching.instance.InteractionControllerInstantiator;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import io.github.kaktushose.jdac.dispatching.middleware.Priority;
import io.github.kaktushose.jdac.dispatching.validation.Validator;
import io.github.kaktushose.jdac.embeds.EmbedConfig;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory;
import io.github.kaktushose.jdac.internal.Helpers;
import io.github.kaktushose.jdac.message.MessageResolver;
import io.github.kaktushose.jdac.message.emoji.EmojiResolver;
import io.github.kaktushose.jdac.message.emoji.EmojiSource;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.i18n.Localizer;
import io.github.kaktushose.jdac.permissions.PermissionsProvider;
import io.github.kaktushose.jdac.scope.GuildScopeProvider;
import io.github.kaktushose.proteus.type.Type;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static io.github.kaktushose.jdac.configuration.Property.FallbackBehaviour.ACCUMULATE;
import static io.github.kaktushose.jdac.configuration.Property.FallbackBehaviour.OVERRIDE;
import static io.github.kaktushose.jdac.internal.Helpers.castUnsafe;

/// # General
/// Properties represent...
/// - config options to adjust the behaviour of JDA-Commands (like [JDACBuilder#shutdownJDA(boolean)])
/// - implementations of exposed services (like [JDACBuilder#localizer(Localizer)])
/// - or services provided by the framework that are exposed to the user (like [I18n])
///
/// Although the user can [provide][PropertyProvider] _custom values_ for these properties,
/// the properties itself are all defined by the JDA-Commands and exposed as public static fields in this interface.
///
/// # Categories
/// Properties are primarily categorized by 3 groups:
/// - _user settable_ -> only configurable by using the designated [JDACBuilder] method
/// - _user settable + loadable by extension_ -> above applies plus values can be provided by [Extension]s
/// - _provided_ -> service that are provided by JDA-Commands, the user can use but not create/replace them
///
/// # Types
/// Additionally, there are 3 types of properties:
/// - [Singleton] -> the property will have the value of the provider with the highest [priority][PropertyProvider#priority()]
/// - [Mapping] -> the property represents a Map with a key and value. All [providers][PropertyProvider] will be
///                accumulated and providers with higher [priorities][PropertyProvider#priority()] take precedence
/// - [Enumeration] -> the property represents a [Collection]. All [providers][PropertyProvider] will be accumulated
///
/// The only exception to this general accumulation rule are fallback values. Whether they are accumulated with other
/// providers or overwritten is defined by [Property#fallbackBehaviour()].
///
@SuppressWarnings("unused")
public sealed interface Property<T> permits Property.Enumeration, Property.Singleton, Property.Mapping {

    /// Whether the fallback values provided by JDA-Commands should be accumulated together
    /// with other user provided [PropertyProvider]s for this property.
    ///
    /// @return the fallback behaviour
    FallbackBehaviour fallbackBehaviour();

    /// The name of the property is a user and machine-readable unique identifier for a property
    ///
    /// @return the name of the property
    String name();

    /// The category of the property defines the way a value for it can be provided.
    ///
    /// @return the category of the property
    Category category();


    /// The fallback behaviour defines whether the fallback value provided by JDA-Commands should be
    /// accumulated together with the user defined ones or overwritten.
    /// Applies only to [Enumeration] and [Mapping].
    enum FallbackBehaviour {
        /// the fallback value should be overridden
        OVERRIDE,

        /// the fallback value should be accumulated with the user provided ones
        ACCUMULATE
    }

    /// The category of a property defines how values for it can be provided.
    enum Category {

        /// the value of this property can only be provided by JDA-Commands itself.
        /// Applies to services like [MessageResolver] or [I18n]
        PROVIDED,

        /// the value of this property can only be provided by the user using methods in [JDACBuilder]
        USER,

        /// the value of this property can be provided by either the user using [JDACBuilder] or/and
        /// loaded from [Extension]s
        EXTENSION
    }


    /// settable by user + loadable by extension
    Property<Collection<ClassFinder>> CLASS_FINDER =
            new Enumeration<>("CLASS_FINDER", Category.EXTENSION, ClassFinder.class, OVERRIDE);

    Property<Collection<EmojiSource>> EMOJI_SOURCES =
            new Enumeration<>("EMOJI_SOURCES", Category.EXTENSION, EmojiSource.class, OVERRIDE);

    Property<Descriptor> DESCRIPTOR =
            new Singleton<>("DESCRIPTOR", Category.EXTENSION, Descriptor.class);

    Property<Localizer> LOCALIZER =
            new Singleton<>("LOCALIZER", Category.EXTENSION, Localizer.class);

    Property<InteractionControllerInstantiator> INTERACTION_CONTROLLER_INSTANTIATOR =
            new Singleton<>("INTERACTION_CONTROLLER_INSTANTIATOR", Category.EXTENSION, InteractionControllerInstantiator.class);

    Property<Collection<Map.Entry<Priority, Middleware>>> MIDDLEWARE =
            new Enumeration<>("MIDDLEWARE", Category.EXTENSION, castUnsafe(Map.Entry.class), ACCUMULATE);

    Property<Map<Map.Entry<Type<?>, Type<?>>, TypeAdapter<?, ?>>> TYPE_ADAPTER =
            new Mapping<>("TYPE_ADAPTER", Category.EXTENSION, castUnsafe(Map.Entry.class), castUnsafe(TypeAdapter.class), ACCUMULATE);

    Property<Map<Class<? extends Annotation>, Validator<?, ?>>> VALIDATOR =
            new Mapping<>("VALIDATOR", Category.EXTENSION, castUnsafe(Class.class), castUnsafe(Validator.class), ACCUMULATE);

    Property<PermissionsProvider> PERMISSION_PROVIDER =
            new Singleton<>("PERMISSION_PROVIDER", Category.EXTENSION, PermissionsProvider.class);

    Property<ErrorMessageFactory> ERROR_MESSAGE_FACTORY =
            new Singleton<>("ERROR_MESSAGE_FACTORY", Category.EXTENSION, ErrorMessageFactory.class);

    Property<GuildScopeProvider> GUILD_SCOPE_PROVIDER =
            new Singleton<>("GUILD_SCOPE_PROVIDER", Category.EXTENSION, GuildScopeProvider.class);

    /// only user settable
     Property<CommandDefinition.CommandConfig> GLOBAL_COMMAND_CONFIG =
            new Singleton<>("GLOBAL_COMMAND_CONFIG", Category.USER, CommandDefinition.CommandConfig.class);

     Property<InteractionDefinition.ReplyConfig> GLOBAL_REPLY_CONFIG =
            new Singleton<>("GLOBAL_REPLY_CONFIG", Category.USER, InteractionDefinition.ReplyConfig.class);

     Property<Collection<String>> PACKAGES =
            new Enumeration<>("PACKAGES", Category.USER, String.class, ACCUMULATE);

     Property<ExpirationStrategy> EXPIRATION_STRATEGY =
            new Singleton<>("EXPIRATION_STRATEGY", Category.USER, ExpirationStrategy.class);

     Property<Boolean> LOCALIZE_COMMANDS =
            new Singleton<>("LOCALIZE_COMMANDS", Category.USER, Boolean.class);

     Property<Boolean> SHUTDOWN_JDA =
            new Singleton<>("SHUTDOWN_JDA", Category.USER, Boolean.class);

     Property<Map<Class<? extends Extension.Data>, Extension.Data>> EXTENSION_DATA =
            new Mapping<>("EXTENSION_DATA", Category.USER, castUnsafe(Class.class), Extension.Data.class, ACCUMULATE);

    Property<ExtensionFilter> EXTENSION_FILTER =
            new Property.Singleton<>("EXTENSION_FILTER", Property.Category.USER, ExtensionFilter.class);

    Property<Consumer<EmbedConfig>> EMBED_CONFIG =
            new Property.Singleton<>("EMBED_CONFIG", Property.Category.USER, Helpers.castUnsafe(Consumer.class));

    /// only created
     Property<I18n> I18N =
            new Singleton<>("I18N", Category.PROVIDED, I18n.class);

     Property<MessageResolver> MESSAGE_RESOLVER =
            new Singleton<>("MESSAGE_RESOLVER", Category.PROVIDED, MessageResolver.class);

     Property<EmojiResolver> EMOJI_RESOLVER =
            new Singleton<>("EMOJI_RESOLVER", Category.PROVIDED, EmojiResolver.class);

     Property<ClassFinder> MERGED_CLASS_FINDER =
             new Singleton<>("MERGED_CLASS_FINDER", Category.PROVIDED, ClassFinder.class);


     Collection<Property<?>> LOADABLE = List.of(
             CLASS_FINDER,
             EMOJI_SOURCES,
             DESCRIPTOR,
             LOCALIZER,
             INTERACTION_CONTROLLER_INSTANTIATOR,
             MIDDLEWARE,
             TYPE_ADAPTER,
             VALIDATOR,
             PERMISSION_PROVIDER,
             ERROR_MESSAGE_FACTORY,
             GUILD_SCOPE_PROVIDER
     );

    record Mapping<K, V>(String name, Category category, Class<K> key, Class<V> value,
                         FallbackBehaviour fallbackBehaviour) implements Property<Map<K, V>> {}

    record Singleton<T>(String name, Category category, Class<T> type) implements Property<T> {
        @Override
        public FallbackBehaviour fallbackBehaviour() {
            throw new UnsupportedOperationException("fallback behaviour not supported on Property.Singleton");
        }
    }

    record Enumeration<E>(String name, Category category, Class<E> type,
                          FallbackBehaviour fallbackBehaviour) implements Property<Collection<E>> {}
}
