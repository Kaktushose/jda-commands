package io.github.kaktushose.jdac.configuration;

import io.github.kaktushose.jdac.configuration.type.Enumeration;
import io.github.kaktushose.jdac.configuration.type.Instance;
import io.github.kaktushose.jdac.configuration.type.Mapping;
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
import java.util.Map;

import static io.github.kaktushose.jdac.configuration.PropertyType.FallbackBehaviour.ACCUMULATE;
import static io.github.kaktushose.jdac.configuration.PropertyType.FallbackBehaviour.OVERRIDE;

public sealed interface PropertyType<T> permits Enumeration, Instance, Mapping {
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
    PropertyType<Collection<ClassFinder>> CLASS_FINDER =
            new Enumeration<>("CLASS_FINDER", PropertyType.Scope.EXTENSION, ClassFinder.class, OVERRIDE);

    PropertyType<Collection<EmojiSource>> EMOJI_SOURCES =
            new Enumeration<>("EMOJI_SOURCES", PropertyType.Scope.EXTENSION, EmojiSource.class, OVERRIDE);

    PropertyType<Descriptor> DESCRIPTOR =
            new Instance<>("DESCRIPTOR", PropertyType.Scope.EXTENSION, Descriptor.class);

    PropertyType<Localizer> LOCALIZER =
            new Instance<>("LOCALIZER", PropertyType.Scope.EXTENSION, Localizer.class);

    PropertyType<InteractionControllerInstantiator> INTERACTION_CONTROLLER_INSTANTIATOR =
            new Instance<>("INTERACTION_CONTROLLER_INSTANTIATOR", PropertyType.Scope.EXTENSION, InteractionControllerInstantiator.class);

    PropertyType<Collection<Map.Entry<Priority, Middleware>>> MIDDLEWARE =
            new Enumeration<>("MIDDLEWARE", PropertyType.Scope.EXTENSION, castUnsafe(Map.Entry.class), ACCUMULATE);

    PropertyType<Map<Map.Entry<Type<?>, Type<?>>, TypeAdapter<?, ?>>> TYPE_ADAPTER =
            new Mapping<>("TYPE_ADAPTER", PropertyType.Scope.EXTENSION, castUnsafe(Map.Entry.class), castUnsafe(TypeAdapter.class), ACCUMULATE);

    PropertyType<Map<Class<? extends Annotation>, Validator<?, ?>>> VALIDATOR =
            new Mapping<>("VALIDATOR", PropertyType.Scope.EXTENSION, castUnsafe(Class.class), castUnsafe(Validator.class), ACCUMULATE);

    PropertyType<PermissionsProvider> PERMISSION_PROVIDER =
            new Instance<>("PERMISSION_PROVIDER", PropertyType.Scope.EXTENSION, PermissionsProvider.class);

    PropertyType<ErrorMessageFactory> ERROR_MESSAGE_FACTORY =
            new Instance<>("ERROR_MESSAGE_FACTORY", PropertyType.Scope.EXTENSION, ErrorMessageFactory.class);

    PropertyType<GuildScopeProvider> GUILD_SCOPE_PROVIDER =
            new Instance<>("GUILD_SCOPE_PROVIDER", PropertyType.Scope.EXTENSION, GuildScopeProvider.class);



    /// only user settable
     PropertyType<CommandDefinition.CommandConfig> GLOBAL_COMMAND_CONFIG =
            new Instance<>("GLOBAL_COMMAND_CONFIG", Scope.USER, CommandDefinition.CommandConfig.class);

     PropertyType<InteractionDefinition.ReplyConfig> GLOBAL_REPLY_CONFIG =
            new Instance<>("GLOBAL_REPLY_CONFIG", Scope.USER, InteractionDefinition.ReplyConfig.class);

     PropertyType<Collection<String>> PACKAGES =
            new Enumeration<>("PACKAGES", PropertyType.Scope.USER, String.class, ACCUMULATE);

     PropertyType<ExpirationStrategy> EXPIRATION_STRATEGY =
            new Instance<>("EXPIRATION_STRATEGY", PropertyType.Scope.USER, ExpirationStrategy.class);

     PropertyType<Boolean> LOCALIZE_COMMANDS =
            new Instance<>("LOCALIZE_COMMANDS", PropertyType.Scope.USER, Boolean.class);

     PropertyType<Boolean> SHUTDOWN_JDA =
            new Instance<>("SHUTDOWN_JDA", PropertyType.Scope.USER, Boolean.class);

     PropertyType<Map<Class<? extends Extension.Data>, Extension.Data>> EXTENSION_DATA =
            new Mapping<>("EXTENSION_DATA", PropertyType.Scope.USER, castUnsafe(Class.class), Extension.Data.class, ACCUMULATE);

    /// only created
     PropertyType<I18n> I18N =
            new Instance<>("I18N", PropertyType.Scope.PROVIDED, I18n.class);

     PropertyType<MessageResolver> MESSAGE_RESOLVER =
            new Instance<>("MESSAGE_RESOLVER", PropertyType.Scope.PROVIDED, MessageResolver.class);

     PropertyType<EmojiResolver> EMOJI_RESOLVER =
            new Instance<>("EMOJI_RESOLVER", PropertyType.Scope.PROVIDED, EmojiResolver.class);



    @SuppressWarnings("unchecked")
    private static <T> Class<T> castUnsafe(Class<?> cast) {
        return (Class<T>) cast;
    }
}
