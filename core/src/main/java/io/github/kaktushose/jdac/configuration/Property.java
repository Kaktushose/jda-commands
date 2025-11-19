package io.github.kaktushose.jdac.configuration;

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
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory;
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

import static io.github.kaktushose.jdac.configuration.Property.FallbackBehaviour.ACCUMULATE;
import static io.github.kaktushose.jdac.configuration.Property.FallbackBehaviour.OVERRIDE;
import static io.github.kaktushose.jdac.internal.Helpers.castUnsafe;

@SuppressWarnings("unused")
public sealed interface Property<T> permits Property.Enumeration, Property.Instance, Property.Mapping {
    FallbackBehaviour fallbackBehaviour();
    String name();
    Scope scope();


    enum FallbackBehaviour {
        OVERRIDE,
        ACCUMULATE
    }

    enum Scope {
        PROVIDED,
        USER,
        EXTENSION
    }


    /// settable by user + loadable by extension
    Property<Collection<ClassFinder>> CLASS_FINDER =
            new Enumeration<>("CLASS_FINDER", Scope.EXTENSION, ClassFinder.class, OVERRIDE);

    Property<Collection<EmojiSource>> EMOJI_SOURCES =
            new Enumeration<>("EMOJI_SOURCES", Scope.EXTENSION, EmojiSource.class, OVERRIDE);

    Property<Descriptor> DESCRIPTOR =
            new Instance<>("DESCRIPTOR", Scope.EXTENSION, Descriptor.class);

    Property<Localizer> LOCALIZER =
            new Instance<>("LOCALIZER", Scope.EXTENSION, Localizer.class);

    Property<InteractionControllerInstantiator> INTERACTION_CONTROLLER_INSTANTIATOR =
            new Instance<>("INTERACTION_CONTROLLER_INSTANTIATOR", Scope.EXTENSION, InteractionControllerInstantiator.class);

    Property<Collection<Map.Entry<Priority, Middleware>>> MIDDLEWARE =
            new Enumeration<>("MIDDLEWARE", Scope.EXTENSION, castUnsafe(Map.Entry.class), ACCUMULATE);

    Property<Map<Map.Entry<Type<?>, Type<?>>, TypeAdapter<?, ?>>> TYPE_ADAPTER =
            new Mapping<>("TYPE_ADAPTER", Scope.EXTENSION, castUnsafe(Map.Entry.class), castUnsafe(TypeAdapter.class), ACCUMULATE);

    Property<Map<Class<? extends Annotation>, Validator<?, ?>>> VALIDATOR =
            new Mapping<>("VALIDATOR", Scope.EXTENSION, castUnsafe(Class.class), castUnsafe(Validator.class), ACCUMULATE);

    Property<PermissionsProvider> PERMISSION_PROVIDER =
            new Instance<>("PERMISSION_PROVIDER", Scope.EXTENSION, PermissionsProvider.class);

    Property<ErrorMessageFactory> ERROR_MESSAGE_FACTORY =
            new Instance<>("ERROR_MESSAGE_FACTORY", Scope.EXTENSION, ErrorMessageFactory.class);

    Property<GuildScopeProvider> GUILD_SCOPE_PROVIDER =
            new Instance<>("GUILD_SCOPE_PROVIDER", Scope.EXTENSION, GuildScopeProvider.class);

    /// only user settable
     Property<CommandDefinition.CommandConfig> GLOBAL_COMMAND_CONFIG =
            new Instance<>("GLOBAL_COMMAND_CONFIG", Scope.USER, CommandDefinition.CommandConfig.class);

     Property<InteractionDefinition.ReplyConfig> GLOBAL_REPLY_CONFIG =
            new Instance<>("GLOBAL_REPLY_CONFIG", Scope.USER, InteractionDefinition.ReplyConfig.class);

     Property<Collection<String>> PACKAGES =
            new Enumeration<>("PACKAGES", Scope.USER, String.class, ACCUMULATE);

     Property<ExpirationStrategy> EXPIRATION_STRATEGY =
            new Instance<>("EXPIRATION_STRATEGY", Scope.USER, ExpirationStrategy.class);

     Property<Boolean> LOCALIZE_COMMANDS =
            new Instance<>("LOCALIZE_COMMANDS", Scope.USER, Boolean.class);

     Property<Boolean> SHUTDOWN_JDA =
            new Instance<>("SHUTDOWN_JDA", Scope.USER, Boolean.class);

     Property<Map<Class<? extends Extension.Data>, Extension.Data>> EXTENSION_DATA =
            new Mapping<>("EXTENSION_DATA", Scope.USER, castUnsafe(Class.class), Extension.Data.class, ACCUMULATE);

    /// only created
     Property<I18n> I18N =
            new Instance<>("I18N", Scope.PROVIDED, I18n.class);

     Property<MessageResolver> MESSAGE_RESOLVER =
            new Instance<>("MESSAGE_RESOLVER", Scope.PROVIDED, MessageResolver.class);

     Property<EmojiResolver> EMOJI_RESOLVER =
            new Instance<>("EMOJI_RESOLVER", Scope.PROVIDED, EmojiResolver.class);

     Property<ClassFinder> MERGED_CLASS_FINDER =
             new Instance<>("MERGED_CLASS_FINDER", Scope.PROVIDED, ClassFinder.class);


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

    record Mapping<K, V>(String name, Scope scope, Class<K> key, Class<V> value,
                                FallbackBehaviour fallbackBehaviour) implements Property<Map<K, V>> {}

    record Instance<T>(String name, Scope scope, Class<T> type) implements Property<T> {
        @Override
        public FallbackBehaviour fallbackBehaviour() {
            throw new UnsupportedOperationException("fallback behaviour not supported on Property.Instance");
        }
    }

    record Enumeration<E>(String name, Scope scope, Class<E> type,
                                 FallbackBehaviour fallbackBehaviour) implements Property<Collection<E>> {}
}
