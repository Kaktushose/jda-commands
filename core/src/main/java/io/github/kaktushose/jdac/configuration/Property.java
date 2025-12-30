package io.github.kaktushose.jdac.configuration;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.definitions.description.ClassFinder;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition;
import io.github.kaktushose.jdac.dispatching.adapter.AdapterType;
import io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.dispatching.context.KeyValueStore;
import io.github.kaktushose.jdac.dispatching.events.Event;
import io.github.kaktushose.jdac.dispatching.expiration.ExpirationStrategy;
import io.github.kaktushose.jdac.dispatching.instance.InteractionControllerInstantiator;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import io.github.kaktushose.jdac.dispatching.middleware.Priority;
import io.github.kaktushose.jdac.dispatching.validation.Validator;
import io.github.kaktushose.jdac.embeds.EmbedConfig;
import io.github.kaktushose.jdac.embeds.EmbedDataSource;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory;
import io.github.kaktushose.jdac.introspection.Definitions;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.message.emoji.EmojiResolver;
import io.github.kaktushose.jdac.message.emoji.EmojiSource;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.i18n.Localizer;
import io.github.kaktushose.jdac.message.placeholder.PlaceholderResolver;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import io.github.kaktushose.jdac.permissions.PermissionsProvider;
import io.github.kaktushose.jdac.scope.GuildScopeProvider;
import io.github.kaktushose.proteus.type.Type;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.Consumer;

import static io.github.kaktushose.jdac.configuration.Property.FallbackBehaviour.ACCUMULATE;
import static io.github.kaktushose.jdac.configuration.Property.FallbackBehaviour.OVERRIDE;
import static io.github.kaktushose.jdac.internal.Helpers.castUnsafe;

/// # Properties
/// An [Property] represent either...
/// - a config option to adjust the behaviour of JDA-Commands (like [JDACBuilder#shutdownJDA(boolean)])
/// - an implementation of exposed services (like [Localizer])
/// - or a service provided by the framework that are exposed to the user (like [I18n])
///
/// Although the user can [provide][PropertyProvider] _custom values_ for these properties,
/// the properties itself are all defined by the JDA-Commands and exposed as public static fields in this interface.
///
/// ## Categories
/// Properties are primarily categorized by 3 groups:
/// - [_user settable_][Category#USER_SETTABLE] -> only configurable by using the designated [JDACBuilder] method
/// - [_user settable + loadable from extension_][Category#LOADABLE] -> above applies plus values can be provided by [Extension]s
/// - [_provided_][Category#PROVIDED] -> service that are provided by JDA-Commands, the user can use but not create/replace them
///
/// ## Types
/// Additionally, there are 3 types of properties:
/// - [Singleton] -> the property will have the value of the provider with the highest [priority][PropertyProvider#priority()]
/// - [Map] -> the property represents a Map with a key and value. The value of all [providers][PropertyProvider] will be
///                accumulated and providers with higher [priorities][PropertyProvider#priority()] take precedence
/// - [Enumeration] -> the property represents a [Collection]. The value of all [providers][PropertyProvider] will be accumulated
///
/// The only exception to this general accumulation rule are fallback values. Whether they are accumulated with other
/// providers or overwritten is defined by [Property#fallbackBehaviour()].
///
/// ## Stages
/// A [Property]'s value is only set during some stages during the frameworks runtime and in the process of execution
/// user interactions.
/// To know in what stage a property's value is accessible take a look at the fields [IntrospectionAccess] annotation
///
@SuppressWarnings("unused")
@PropertyProcessed
public sealed interface Property<T> permits Property.Enumeration, Property.Singleton, Property.Map {
    // -------- settable by user + loadable from extension --------

    /// @see JDACBuilder#classFinders(ClassFinder...)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.LOADABLE, fallbackBehaviour = OVERRIDE)
    Property<Collection<ClassFinder>> CLASS_FINDER =
            new Enumeration<>("CLASS_FINDER", Category.LOADABLE, ClassFinder.class, OVERRIDE, Stage.CONFIGURATION);

    /// @see JDACBuilder#emojiSource(EmojiSource...)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.LOADABLE, fallbackBehaviour = OVERRIDE)
    Property<Collection<EmojiSource>> EMOJI_SOURCES =
            new Enumeration<>("EMOJI_SOURCES", Category.LOADABLE, EmojiSource.class, OVERRIDE, Stage.CONFIGURATION);

    /// @see EmbedConfig#sources(EmbedDataSource...)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.LOADABLE, fallbackBehaviour = ACCUMULATE)
    Property<Collection<EmbedDataSource>> EMBED_SOURCES =
            new Enumeration<>("EMBED_SOURCES", Category.LOADABLE, EmbedDataSource.class, ACCUMULATE, Stage.CONFIGURATION);

    /// @see JDACBuilder#descriptor(Descriptor)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.LOADABLE)
    Property<Descriptor> DESCRIPTOR =
            new Singleton<>("DESCRIPTOR", Category.LOADABLE, Descriptor.class, Stage.CONFIGURATION);

    /// @see JDACBuilder#localizer(Localizer)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.LOADABLE)
    Property<Localizer> LOCALIZER =
            new Singleton<>("LOCALIZER", Category.LOADABLE, Localizer.class, Stage.CONFIGURATION);

    /// @see JDACBuilder#instanceProvider(InteractionControllerInstantiator)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.LOADABLE)
    Property<InteractionControllerInstantiator> INTERACTION_CONTROLLER_INSTANTIATOR =
            new Singleton<>("INTERACTION_CONTROLLER_INSTANTIATOR", Category.LOADABLE, InteractionControllerInstantiator.class, Stage.CONFIGURATION);

    /// MIDDLEWARE property holds multiple [Middleware]s associated with their [Priority]
    /// @see JDACBuilder#middleware(Priority, Middleware)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.LOADABLE, fallbackBehaviour = ACCUMULATE)
    Property<Collection<java.util.Map.Entry<Priority, Middleware>>> MIDDLEWARE =
            new Enumeration<>("MIDDLEWARE", Category.LOADABLE, castUnsafe(java.util.Map.Entry.class), ACCUMULATE, Stage.CONFIGURATION);

    /// The TYPE_ADAPTER property maps [AdapterType]s containing the source and targets [Type]s to their associated [TypeAdapter]
    /// @see JDACBuilder#adapter(Class, Class, TypeAdapter)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.LOADABLE, fallbackBehaviour = ACCUMULATE)
    Property<java.util.Map<AdapterType<?, ?>, TypeAdapter<?, ?>>> TYPE_ADAPTER =
            new Map<>("TYPE_ADAPTER", Category.LOADABLE, castUnsafe(AdapterType.class), castUnsafe(TypeAdapter.class), ACCUMULATE, Stage.CONFIGURATION);

    /// The VALIDATOR property maps a [Validator] to its identifying annotation.
    /// @see JDACBuilder#validator(Class, Validator)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.LOADABLE, fallbackBehaviour = ACCUMULATE)
    Property<java.util.Map<Class<? extends Annotation>, Validator<?, ?>>> VALIDATOR =
            new Map<>("VALIDATOR", Category.LOADABLE, castUnsafe(Class.class), castUnsafe(Validator.class), ACCUMULATE, Stage.CONFIGURATION);

    /// @see JDACBuilder#permissionsProvider(PermissionsProvider)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.LOADABLE)
    Property<PermissionsProvider> PERMISSION_PROVIDER =
            new Singleton<>("PERMISSION_PROVIDER", Category.LOADABLE, PermissionsProvider.class, Stage.CONFIGURATION);

    /// @see JDACBuilder#errorMessageFactory(ErrorMessageFactory)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.LOADABLE)
    Property<ErrorMessageFactory> ERROR_MESSAGE_FACTORY =
            new Singleton<>("ERROR_MESSAGE_FACTORY", Category.LOADABLE, ErrorMessageFactory.class, Stage.CONFIGURATION);

    /// @see JDACBuilder#guildScopeProvider(GuildScopeProvider)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.LOADABLE)
    Property<GuildScopeProvider> GUILD_SCOPE_PROVIDER =
            new Singleton<>("GUILD_SCOPE_PROVIDER", Category.LOADABLE, GuildScopeProvider.class, Stage.CONFIGURATION);

    /// @see JDACBuilder#stringResolver(Resolver...)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.LOADABLE, fallbackBehaviour = ACCUMULATE)
    Property<Collection<Resolver<String>>> STRING_RESOLVER =
            new Enumeration<>("STRING_RESOLVER", Category.LOADABLE, castUnsafe(Resolver.class), ACCUMULATE, Stage.CONFIGURATION);

    // -------- user settable --------
    /// @see JDACBuilder#globalCommandConfig(CommandDefinition.CommandConfig)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.USER_SETTABLE)
    Property<CommandDefinition.CommandConfig> GLOBAL_COMMAND_CONFIG =
            new Singleton<>("GLOBAL_COMMAND_CONFIG", Category.USER_SETTABLE, CommandDefinition.CommandConfig.class, Stage.CONFIGURATION);

    /// @see JDACBuilder#globalReplyConfig(InteractionDefinition.ReplyConfig)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.USER_SETTABLE)
    Property<InteractionDefinition.ReplyConfig> GLOBAL_REPLY_CONFIG =
            new Singleton<>("GLOBAL_REPLY_CONFIG", Category.USER_SETTABLE, InteractionDefinition.ReplyConfig.class, Stage.CONFIGURATION);

    /// @see JDACBuilder#packages(String...)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.USER_SETTABLE, fallbackBehaviour = ACCUMULATE)
    Property<Collection<String>> PACKAGES =
            new Enumeration<>("PACKAGES", Category.USER_SETTABLE, String.class, ACCUMULATE, Stage.CONFIGURATION);

    /// @see JDACBuilder#expirationStrategy(ExpirationStrategy)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.USER_SETTABLE)
    Property<ExpirationStrategy> EXPIRATION_STRATEGY =
            new Singleton<>("EXPIRATION_STRATEGY", Category.USER_SETTABLE, ExpirationStrategy.class, Stage.CONFIGURATION);

    /// @see JDACBuilder#localizeCommands(boolean)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.USER_SETTABLE)
    Property<Boolean> LOCALIZE_COMMANDS =
            new Singleton<>("LOCALIZE_COMMANDS", Category.USER_SETTABLE, Boolean.class, Stage.CONFIGURATION);

    /// @see JDACBuilder#shutdownJDA(boolean)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.USER_SETTABLE)
    Property<Boolean> SHUTDOWN_JDA =
            new Singleton<>("SHUTDOWN_JDA", Category.USER_SETTABLE, Boolean.class, Stage.CONFIGURATION);

    /// @see JDACBuilder#extensionData(Extension.Data...)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.USER_SETTABLE, fallbackBehaviour = ACCUMULATE)
    Property<java.util.Map<Class<? extends Extension.Data>, Extension.Data>> EXTENSION_DATA =
            new Map<>("EXTENSION_DATA", Category.USER_SETTABLE, castUnsafe(Class.class), Extension.Data.class, ACCUMULATE, Stage.CONFIGURATION);

    /// @see JDACBuilder#filterExtensions(ExtensionFilter.FilterStrategy, String...)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.USER_SETTABLE)
    Property<ExtensionFilter> EXTENSION_FILTER =
            new Singleton<>("EXTENSION_FILTER", Property.Category.USER_SETTABLE, ExtensionFilter.class, Stage.CONFIGURATION);

    /// @see JDACBuilder#embeds(Consumer)
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.USER_SETTABLE)
    Property<Consumer<EmbedConfig>> EMBED_CONFIG =
            new Singleton<>("EMBED_CONFIG", Property.Category.USER_SETTABLE, castUnsafe(Consumer.class), Stage.CONFIGURATION);

    // -------- provided ------------
    // -------- configuration -------
    /// The [I18n] service provided by JDA-Commands.
    /// Needs the values of [#DESCRIPTOR] and [#LOCALIZER].
    ///
    /// @implNote the [PropertyProvider] for this value is defined in the constructor of [JDACBuilder]
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.PROVIDED)
    Property<I18n> I18N =
            new Singleton<>("I18N", Category.PROVIDED, I18n.class, Stage.CONFIGURATION);

    /// The [MessageResolver] service provided byt JDA-Commands.
    /// Needs the values of [#I18N] and [#EMOJI_RESOLVER].
    ///
    /// @implNote the [PropertyProvider] for this value is defined in the constructor of [JDACBuilder]
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.PROVIDED)
    Property<MessageResolver> MESSAGE_RESOLVER =
            new Singleton<>("MESSAGE_RESOLVER", Category.PROVIDED, MessageResolver.class, Stage.CONFIGURATION);

    /// The [EmojiResolver] service provided by JDA-Commands.
    /// Needs the value of [#EMOJI_RESOLVER].
    ///
    /// @implNote the [PropertyProvider] for this value is defined in the constructor of [JDACBuilder]
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.PROVIDED)
    Property<EmojiResolver> EMOJI_RESOLVER =
            new Singleton<>("EMOJI_RESOLVER", Category.PROVIDED, EmojiResolver.class, Stage.CONFIGURATION);

    /// The [PlaceholderResolver] service provided by JDA-Commands.
    ///
    /// @implNote the [PropertyProvider] for this value is defined in the constructor of [JDACBuilder]
    Property<PlaceholderResolver> PLACEHOLDER_RESOLVER =
            new Singleton<>("PLACEHOLDER_RESOLVER", Category.PROVIDED, PlaceholderResolver.class, Stage.CONFIGURATION);

    /// An [ClassFinder] instance that is backed by all [ClassFinder] of [#CLASS_FINDER].
    /// It will search in all registered [ClassFinder] for the requested class.
    ///
    /// @implNote the [PropertyProvider] for this value is defined in the constructor of [JDACBuilder]
    @PropertyInformation(stage = Stage.CONFIGURATION, category = Category.PROVIDED)
    Property<ClassFinder> MERGED_CLASS_FINDER =
             new Singleton<>("MERGED_CLASS_FINDER", Category.PROVIDED, ClassFinder.class, Stage.CONFIGURATION);

    // ------- initialized --------
    /// The [JDACommands] instance available after fully starting the framework.
    @PropertyInformation(stage = Stage.INITIALIZED, category = Category.PROVIDED)
    Property<JDACommands> JDA_COMMANDS =
            new Singleton<>("JDA_COMMANDS", Category.PROVIDED, JDACommands.class, Stage.INITIALIZED);

    /// The [Definitions] instance available after fully starting the framework.
    @PropertyInformation(stage = Stage.INITIALIZED, category = Category.PROVIDED)
    Property<Definitions> DEFINITIONS =
            new Singleton<>("DEFINITIONS", Category.PROVIDED, Definitions.class, Stage.INITIALIZED);

    // ------- runtime ---------
    /// The [JDA] instance bound to this specific Runtime.
    @PropertyInformation(stage = Stage.RUNTIME, category = Category.PROVIDED)
    Property<JDA> JDA =
            new Singleton<>("JDA", Category.PROVIDED, JDA.class, Stage.RUNTIME);

    /// The identifier bound to this runtime. Same as [Event#runtimeId()].
    @PropertyInformation(stage = Stage.RUNTIME, category = Category.PROVIDED)
    Property<String> RUNTIME_ID =
            new Singleton<>("RUNTIME_ID", Category.PROVIDED, String.class, Stage.RUNTIME);

    /// The [KeyValueStore] associated with this runtime. Same as [InvocationContext#keyValueStore()] or [Event#kv()].
    @PropertyInformation(stage = Stage.RUNTIME, category = Category.PROVIDED)
    Property<KeyValueStore> KEY_VALUE_STORE =
            new Singleton<>("KEY_VALUE_STORE", Category.PROVIDED, KeyValueStore.class, Stage.RUNTIME);


    // ------ interaction ---------
    /// The [GenericInteractionCreateEvent] of this interaction. Same as [Event#jdaEvent()].
    @PropertyInformation(stage = Stage.INTERACTION, category = Category.PROVIDED)
    Property<GenericInteractionCreateEvent> JDA_EVENT =
            new Singleton<>("JDA_EVENT", Category.PROVIDED, GenericInteractionCreateEvent.class, Stage.INTERACTION);

    /// The [InvocationContext] of this interaction. Same as in [Middleware#accept(InvocationContext)].
    @PropertyInformation(stage = Stage.INTERACTION, category = Category.PROVIDED)
    Property<InvocationContext<?>> INVOCATION_CONTEXT =
            new Singleton<>("INVOCATION_CONTEXT", Category.PROVIDED, castUnsafe(InvocationContext.class), Stage.INTERACTION);


    /// A collection consisting of all [Property]s that are
    /// [settable by the user and loadable through extensions][Category#LOADABLE]
    ///
    /// @see Category#LOADABLE
    Collection<Property<?>> LOADABLE = PropertyListAccessor.getLoadable(); // automatically generated by 'processor' module

    /// A collection consisting of all [Property]s that are [settable by the user][Category#USER_SETTABLE]
    ///
    /// @see Category#USER_SETTABLE
    Collection<Property<?>> USER_SETTABLE = PropertyListAccessor.getSettable(); // automatically generated by 'processor' module

    /// A collection consisting of all [Property]s that are [provided by JDA-Commands][Category#PROVIDED]
    ///
    /// @see Category#PROVIDED
    Collection<Property<?>> PROVIDED = PropertyListAccessor.getProvided(); // automatically generated by 'processor' module

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

    /// The stage or "level" at which this property has a value set.
    Stage stage();

    /// The fallback behaviour defines whether the fallback value provided by JDA-Commands should be
    /// accumulated together with the user defined ones or overwritten.
    /// Applies only to [Enumeration] and [Map].
    enum FallbackBehaviour {
        /// the fallback value should be overridden
        OVERRIDE,

        /// the fallback value should be accumulated with the user provided ones
        ACCUMULATE,

        /// the Property is of type [Singleton] and doesn't have one
        NONE
    }

    /// The category of a property defines how values for it can be provided.
    enum Category {

        /// the value of this property can only be provided by JDA-Commands itself.
        /// Applies to services like [MessageResolver] or [I18n]
        ///
        /// @see Property#PROVIDED
        PROVIDED,

        /// the value of this property can only be provided by the user using methods in [JDACBuilder]
        ///
        /// @see Property#USER_SETTABLE
        USER_SETTABLE,

        /// the value of this property can be provided by either the user using [JDACBuilder] or/and
        /// loaded from [Extension]s
        ///
        /// @see Property#LOADABLE
        LOADABLE
    }

    /// A [Map] property basically represents a [java.util.Map], where each value belongs target a specific key.
    ///
    /// @param category the [Category] of this property
    /// @param key the key type
    /// @param value the value type
    /// @param name the property's name
    /// @param fallbackBehaviour the property's [FallbackBehaviour]
    /// @param stage the property's stage
    record Map<K, V>(String name, Category category, Class<K> key, Class<V> value,
                     FallbackBehaviour fallbackBehaviour, Stage stage) implements Property<java.util.Map<K, V>> {}

    /// A [Singleton] property just hols one value. The value with the highest priority takes precedence.
    ///
    /// @param name the property's name
    /// @param category the property's category
    /// @param type the property's type
    /// @param stage the property's stage
    record Singleton<T>(String name, Category category, Class<T> type, Stage stage) implements Property<T> {
        @Override
        public FallbackBehaviour fallbackBehaviour() {
            return FallbackBehaviour.NONE;
        }
    }

    /// A [Enumeration] property basically represents a [Collection] of multiple values.
    ///
    /// @param category the property's category
    /// @param name the property's name
    /// @param type the property's type
    /// @param fallbackBehaviour the property's [FallbackBehaviour]
    /// @param stage the property's stage
    record Enumeration<E>(String name, Category category, Class<E> type,
                          FallbackBehaviour fallbackBehaviour, Stage stage) implements Property<Collection<E>> {}
}
