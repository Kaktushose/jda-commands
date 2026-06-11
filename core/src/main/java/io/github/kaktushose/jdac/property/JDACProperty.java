package io.github.kaktushose.jdac.property;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.JDACommands;
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
import io.github.kaktushose.jdac.dispatching.instance.Instantiator;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import io.github.kaktushose.jdac.dispatching.middleware.Priority;
import io.github.kaktushose.jdac.dispatching.validation.Validator;
import io.github.kaktushose.jdac.embeds.EmbedConfig;
import io.github.kaktushose.jdac.embeds.EmbedDataSource;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory;
import io.github.kaktushose.jdac.message.emoji.EmojiResolver;
import io.github.kaktushose.jdac.message.emoji.EmojiSource;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.i18n.Localizer;
import io.github.kaktushose.jdac.message.placeholder.PlaceholderResolver;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import io.github.kaktushose.jdac.message.resolver.Resolver;
import io.github.kaktushose.jdac.permissions.PermissionsProvider;
import io.github.kaktushose.jdac.processor.property.api.PropertyProcessed;
import io.github.kaktushose.jdac.property.extension.Extension;
import io.github.kaktushose.jdac.property.extension.ExtensionFilter;
import io.github.kaktushose.jdac.property.internal.JDACEnumerationProperty;
import io.github.kaktushose.jdac.property.internal.JDACMappingProperty;
import io.github.kaktushose.jdac.property.internal.JDACSingletonProperty;
import io.github.kaktushose.jdac.scope.GuildScopeProvider;
import io.github.kaktushose.proteus.type.Type;
import dev.goldmensch.propane.property.*;
import dev.goldmensch.propane.property.Property.Source;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import static dev.goldmensch.propane.property.Property.FallbackStrategy.COMBINE;
import static dev.goldmensch.propane.property.Property.FallbackStrategy.IGNORE;
import static io.github.kaktushose.jdac.internal.Helpers.castUnsafe;


/// A [JDACProperty] is an identifier for a certain component of a JDA-Commands.
///
/// A JDA-Commands component is just some sort of arbitrary information. That could be some service like [MessageResolver],
/// some setting configured by the user via a builder (like [JDACBuilder#localizeCommands(boolean)]), a custom implementation of an interface
/// (like [Instantiator]) or something else.
///
/// There are basically 3 types of properties:
///
/// - [Singleton][SingletonPropertySkeleton] for values consisting of one instance
/// - [Enumeration][EnumerationPropertySkeleton] for values consisting of multiple instances (reference to [Collection])
/// - [Mapping][MappingPropertySkeleton] for values representing a mapping between keys and values (reference to [Map])
///
/// Beside their purpose, each type hold a slightly different set of information, but they share some common
/// values:
///     - a unique [name][Property#name()]
///     - a [scope][JDACScope] to which this property is bound
///     - a [source][Property.Source], from which the values of this property can be retrieved.
///
/// @see JDACIntrospection
/// @see Property
/// @see SpecificProperty
@PropertyProcessed
public interface JDACProperty<T> extends SpecificProperty<T> {
    // -------- settable by user + loadable from extension --------

    /// @see JDACBuilder#classFinders(ClassFinder...)
    @PropertyInformation(scope = JDACScope.CONFIGURATION,
            source = Property.Source.EXTENSION,
            fallbackBehaviour = IGNORE)
    JDACProperty<Collection<ClassFinder>> CLASS_FINDER =
            new JDACEnumerationProperty<>("CLASS_FINDER", Property.Source.EXTENSION, JDACScope.CONFIGURATION, ClassFinder.class, IGNORE);

    /// @see JDACBuilder#emojiSource(EmojiSource...)
    @PropertyInformation(scope = JDACScope.CONFIGURATION,
            source = Property.Source.EXTENSION,
            fallbackBehaviour = IGNORE)
    JDACProperty<Collection<EmojiSource>> EMOJI_SOURCES =
            new JDACEnumerationProperty<>("EMOJI_SOURCES", Property.Source.EXTENSION, JDACScope.CONFIGURATION, EmojiSource.class, IGNORE);

    /// @see EmbedConfig#sources(EmbedDataSource...)
    @PropertyInformation(scope = JDACScope.CONFIGURATION,
            source = Property.Source.EXTENSION,
            fallbackBehaviour = COMBINE)
    JDACProperty<Collection<EmbedDataSource>> EMBED_SOURCES =
            new JDACEnumerationProperty<>("EMBED_SOURCES", Property.Source.EXTENSION, JDACScope.CONFIGURATION, EmbedDataSource.class, COMBINE);

    /// @see JDACBuilder#descriptor(Descriptor)
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.EXTENSION)
    JDACProperty<Descriptor> DESCRIPTOR =
            new JDACSingletonProperty<>("DESCRIPTOR", Property.Source.EXTENSION, JDACScope.CONFIGURATION, Descriptor.class);

    /// @see JDACBuilder#localizer(Localizer)
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.EXTENSION)
    JDACProperty<Localizer> LOCALIZER =
            new JDACSingletonProperty<>("LOCALIZER", Property.Source.EXTENSION, JDACScope.CONFIGURATION, Localizer.class);

    /// @see JDACBuilder#instantiator(Instantiator)
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.EXTENSION)
    JDACProperty<Instantiator> INSTANTIATOR = new JDACSingletonProperty<>("INSTANTIATOR", Property.Source.EXTENSION, JDACScope.CONFIGURATION, Instantiator.class);

    /// MIDDLEWARE property holds multiple [Middleware]s associated with their [Priority]
    ///
    /// @see JDACBuilder#middleware(Priority, Middleware)
    @PropertyInformation(scope = JDACScope.CONFIGURATION,
            source = Property.Source.EXTENSION,
            fallbackBehaviour = COMBINE)
    JDACProperty<Collection<Map.Entry<Priority, Middleware>>> MIDDLEWARE =
            new JDACEnumerationProperty<>("MIDDLEWARE", Property.Source.EXTENSION, JDACScope.CONFIGURATION, castUnsafe(Map.Entry.class), COMBINE);

    /// The TYPE_ADAPTER property maps [AdapterType]s containing the source and targets [Type]s to their associated [TypeAdapter]
    ///
    /// @see JDACBuilder#adapter(Class, Class, TypeAdapter)
    @PropertyInformation(scope = JDACScope.CONFIGURATION,
            source = Property.Source.EXTENSION,
            fallbackBehaviour = COMBINE)
    JDACProperty<Map<AdapterType<?, ?>, TypeAdapter<?, ?>>> TYPE_ADAPTER =
            new JDACMappingProperty<>("TYPE_ADAPTER", Property.Source.EXTENSION, JDACScope.CONFIGURATION, castUnsafe(AdapterType.class), castUnsafe(TypeAdapter.class), COMBINE);

    /// The VALIDATOR property maps a [Validator] to its identifying annotation.
    ///
    /// @see JDACBuilder#validator(Class, Validator)
    @PropertyInformation(scope = JDACScope.CONFIGURATION,
            source = Property.Source.EXTENSION,
            fallbackBehaviour = COMBINE)
    JDACProperty<Map<Class<? extends Annotation>, Validator<?, ?>>> VALIDATOR =
            new JDACMappingProperty<>("VALIDATOR", Property.Source.EXTENSION, JDACScope.CONFIGURATION, castUnsafe(Class.class), castUnsafe(Validator.class), COMBINE);

    /// @see JDACBuilder#permissionsProvider(PermissionsProvider)
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.EXTENSION)
    JDACProperty<PermissionsProvider> PERMISSION_PROVIDER =
            new JDACSingletonProperty<>("PERMISSION_PROVIDER", Property.Source.EXTENSION, JDACScope.CONFIGURATION, PermissionsProvider.class);

    /// @see JDACBuilder#errorMessageFactory(ErrorMessageFactory)
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.EXTENSION)
    JDACProperty<ErrorMessageFactory> ERROR_MESSAGE_FACTORY =
            new JDACSingletonProperty<>("ERROR_MESSAGE_FACTORY", Property.Source.EXTENSION, JDACScope.CONFIGURATION, ErrorMessageFactory.class);

    /// @see JDACBuilder#guildScopeProvider(GuildScopeProvider)
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.EXTENSION)
    JDACProperty<GuildScopeProvider> GUILD_SCOPE_PROVIDER =
            new JDACSingletonProperty<>("GUILD_SCOPE_PROVIDER", Property.Source.EXTENSION, JDACScope.CONFIGURATION, GuildScopeProvider.class);

    /// @see JDACBuilder#stringResolver(Resolver...)
    @PropertyInformation(scope = JDACScope.CONFIGURATION,
            source = Property.Source.EXTENSION,
            fallbackBehaviour = COMBINE)
    JDACProperty<Collection<Resolver<String>>> STRING_RESOLVER =
            new JDACEnumerationProperty<>("STRING_RESOLVER", Property.Source.EXTENSION, JDACScope.CONFIGURATION, castUnsafe(Resolver.class), COMBINE);

    // -------- user settable --------
    /// @see JDACBuilder#globalCommandConfig(CommandDefinition.CommandConfig)
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.BUILDER)
    JDACProperty<CommandDefinition.CommandConfig> GLOBAL_COMMAND_CONFIG =
            new JDACSingletonProperty<>("GLOBAL_COMMAND_CONFIG", Property.Source.BUILDER, JDACScope.CONFIGURATION, CommandDefinition.CommandConfig.class);

    /// @see JDACBuilder#globalReplyConfig(InteractionDefinition.ReplyConfig)
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.BUILDER)
    JDACProperty<InteractionDefinition.ReplyConfig> GLOBAL_REPLY_CONFIG =
            new JDACSingletonProperty<>("GLOBAL_REPLY_CONFIG", Property.Source.BUILDER, JDACScope.CONFIGURATION, InteractionDefinition.ReplyConfig.class);

    /// @see JDACBuilder#packages(String...)
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.BUILDER, fallbackBehaviour = COMBINE)
    JDACProperty<Collection<String>> PACKAGES =
            new JDACEnumerationProperty<>("PACKAGES", Property.Source.BUILDER, JDACScope.CONFIGURATION, String.class, COMBINE);

    /// @see JDACBuilder#expirationStrategy(ExpirationStrategy)
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.BUILDER)
    JDACProperty<ExpirationStrategy> EXPIRATION_STRATEGY =
            new JDACSingletonProperty<>("EXPIRATION_STRATEGY", Property.Source.BUILDER, JDACScope.CONFIGURATION, ExpirationStrategy.class);

    /// @see JDACBuilder#localizeCommands(boolean)
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.BUILDER)
    JDACProperty<Boolean> LOCALIZE_COMMANDS =
            new JDACSingletonProperty<>("LOCALIZE_COMMANDS", Property.Source.BUILDER, JDACScope.CONFIGURATION, Boolean.class);

    /// @see JDACBuilder#shutdownJDA(boolean)
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.BUILDER)
    JDACProperty<Boolean> SHUTDOWN_JDA =
            new JDACSingletonProperty<>("SHUTDOWN_JDA", Property.Source.BUILDER, JDACScope.CONFIGURATION, Boolean.class);

    /// @see JDACBuilder#extensionData(Extension.Data...)
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.BUILDER, fallbackBehaviour = COMBINE)
    JDACProperty<Map<Class<? extends Extension.Data>, Extension.Data>> EXTENSION_DATA =
            new JDACMappingProperty<>("EXTENSION_DATA", Property.Source.BUILDER, JDACScope.CONFIGURATION, castUnsafe(Class.class), Extension.Data.class, COMBINE);

    /// @see JDACBuilder#filterExtensions(ExtensionFilter.FilterStrategy, String...)
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.BUILDER)
    JDACProperty<ExtensionFilter> EXTENSION_FILTER =
            new JDACSingletonProperty<>("EXTENSION_FILTER", Property.Source.BUILDER, JDACScope.CONFIGURATION, ExtensionFilter.class);

    /// @see JDACBuilder#embeds(Consumer)
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.BUILDER)
    JDACProperty<Consumer<EmbedConfig>> EMBED_CONFIG =
            new JDACSingletonProperty<>("EMBED_CONFIG", Property.Source.BUILDER, JDACScope.CONFIGURATION, castUnsafe(Consumer.class));

    // -------- provided ------------
    // -------- configuration -------
    /// The [JDACIntrospection] instance itself used to retrieve properties in this scope.
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.PROVIDED)
    JDACProperty<JDACIntrospection> INTROSPECTION =
            new JDACSingletonProperty<>("INTROSPECTION", Property.Source.PROVIDED, JDACScope.CONFIGURATION, JDACIntrospection.class);

    /// The [I18n] service provided by JDA-Commands.
    /// Needs the values of [#DESCRIPTOR] and [#LOCALIZER].
    ///
    /// @implNote the [JDACPropertyProvider] for this value is defined in the constructor of [JDACBuilder]
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.PROVIDED)
    JDACProperty<I18n> I18N =
            new JDACSingletonProperty<>("I18N", Property.Source.PROVIDED, JDACScope.CONFIGURATION, I18n.class);

    /// The [MessageResolver] service provided byt JDA-Commands.
    /// Needs the values of [#I18N] and [#EMOJI_RESOLVER].
    ///
    /// @implNote the [JDACPropertyProvider] for this value is defined in the constructor of [JDACBuilder]
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.PROVIDED)
    JDACProperty<MessageResolver> MESSAGE_RESOLVER =
            new JDACSingletonProperty<>("MESSAGE_RESOLVER", Property.Source.PROVIDED, JDACScope.CONFIGURATION, MessageResolver.class);

    /// The [EmojiResolver] service provided by JDA-Commands.
    /// Needs the value of [#EMOJI_RESOLVER].
    ///
    /// @implNote the [JDACPropertyProvider] for this value is defined in the constructor of [JDACBuilder]
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.PROVIDED)
    JDACProperty<EmojiResolver> EMOJI_RESOLVER =
            new JDACSingletonProperty<>("EMOJI_RESOLVER", Property.Source.PROVIDED, JDACScope.CONFIGURATION, EmojiResolver.class);

    /// The [PlaceholderResolver] service provided by JDA-Commands.
    ///
    /// @implNote the [JDACPropertyProvider] for this value is defined in the constructor of [JDACBuilder]
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.PROVIDED)
    JDACProperty<PlaceholderResolver> PLACEHOLDER_RESOLVER =
            new JDACSingletonProperty<>("PLACEHOLDER_RESOLVER", Property.Source.PROVIDED, JDACScope.CONFIGURATION, PlaceholderResolver.class);

    /// An [ClassFinder] instance that is backed by all [ClassFinder] of [#CLASS_FINDER].
    /// It will search in all registered [ClassFinder] for the requested class.
    ///
    /// @implNote the [JDACPropertyProvider] for this value is defined in the constructor of [JDACBuilder]
    @PropertyInformation(scope = JDACScope.CONFIGURATION, source = Property.Source.PROVIDED)
    JDACProperty<ClassFinder> MERGED_CLASS_FINDER =
            new JDACSingletonProperty<>("MERGED_CLASS_FINDER", Property.Source.PROVIDED, JDACScope.CONFIGURATION, ClassFinder.class);

    // ------- initialized --------
    /// The [JDACommands] instance available after fully starting the framework.
    @PropertyInformation(scope = JDACScope.INITIALIZED, source = Property.Source.PROVIDED)
    JDACProperty<JDACommands> JDA_COMMANDS =
            new JDACSingletonProperty<>("JDA_COMMANDS", Property.Source.PROVIDED, JDACScope.INITIALIZED, JDACommands.class);

    /// The [Definitions] instance available after fully starting the framework.
    @PropertyInformation(scope = JDACScope.INITIALIZED, source = Property.Source.PROVIDED)
    JDACProperty<Definitions> DEFINITIONS =
            new JDACSingletonProperty<>("DEFINITIONS", Property.Source.PROVIDED, JDACScope.INITIALIZED, Definitions.class);

    /// The [LocalizationFunction] backed by [MessageResolver] used to localize/resolve commands and descriptions.
    ///
    /// @see LocalizationFunction
    @PropertyInformation(scope = JDACScope.INITIALIZED, source = Property.Source.PROVIDED)
    JDACProperty<LocalizationFunction> LOCALIZATION_FUNCTION =
            new JDACSingletonProperty<>("LOCALIZATION_FUNCTION", Property.Source.PROVIDED, JDACScope.INITIALIZED, LocalizationFunction.class);

    // ------- runtime ---------
    /// The [JDA] instance bound to this specific Runtime.
    @PropertyInformation(scope = JDACScope.RUNTIME, source = Property.Source.PROVIDED)
    JDACProperty<JDA> JDA =
            new JDACSingletonProperty<>("JDA", Property.Source.PROVIDED, JDACScope.RUNTIME, JDA.class);

    /// The identifier bound to this runtime. Same as [Event#runtimeId()].
    @PropertyInformation(scope = JDACScope.RUNTIME, source = Property.Source.PROVIDED)
    JDACProperty<Long> RUNTIME_ID =
            new JDACSingletonProperty<>("RUNTIME_ID", Property.Source.PROVIDED, JDACScope.RUNTIME, Long.class);

    /// The [KeyValueStore] associated with this runtime. Same as [InvocationContext#keyValueStore()] or [Event#keyValueStore()()].
    @PropertyInformation(scope = JDACScope.RUNTIME, source = Property.Source.PROVIDED)
    JDACProperty<KeyValueStore> KEY_VALUE_STORE =
            new JDACSingletonProperty<>("KEY_VALUE_STORE", Property.Source.PROVIDED, JDACScope.RUNTIME, KeyValueStore.class);

    // ------ preparation ---------
    /// The [GenericInteractionCreateEvent] of this interaction. Same as [Event#jdaEvent()].
    @PropertyInformation(scope = JDACScope.PREPARATION, source = Property.Source.PROVIDED)
    JDACProperty<GenericInteractionCreateEvent> JDA_EVENT =
            new JDACSingletonProperty<>("JDA_EVENT", Property.Source.PROVIDED, JDACScope.PREPARATION, GenericInteractionCreateEvent.class);

    // ------ interaction ---------
    /// The [InvocationContext] of this interaction. Same as in [Middleware#accept(InvocationContext)].
    @PropertyInformation(scope = JDACScope.INTERACTION, source = Property.Source.PROVIDED)
    JDACProperty<InvocationContext<?>> INVOCATION_CONTEXT =
            new JDACSingletonProperty<>("INVOCATION_CONTEXT", Property.Source.PROVIDED, JDACScope.INTERACTION, castUnsafe(InvocationContext.class));

    /// A collection consisting of all [Property]s that are
    /// [settable by the user and loadable through extensions][Source#EXTENSION]
    ///
    /// @see Source#EXTENSION
    Collection<JDACProperty<?>> EXTENSION = PropertyListAccessor.getExtension();

    /// A collection consisting of all [Property]s that are [settable by the user through JDACBuilder][Source#BUILDER]
    ///
    /// @see Source#BUILDER
    Collection<JDACProperty<?>> BUILDER = PropertyListAccessor.getBuilder();

    /// A collection consisting of all [Property]s that are [provided by JDA-Commands][Source#PROVIDED]
    ///
    /// @see Source#PROVIDED
    Collection<JDACProperty<?>> PROVIDED = PropertyListAccessor.getProvided();
}
