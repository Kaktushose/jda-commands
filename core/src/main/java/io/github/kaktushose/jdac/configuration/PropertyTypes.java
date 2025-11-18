package io.github.kaktushose.jdac.configuration;

import io.github.kaktushose.jdac.JDAContext;
import io.github.kaktushose.jdac.configuration.PropertyType.Mapping;
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
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.extension.Extension;
import io.github.kaktushose.jdac.extension.internal.ExtensionFilter;
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

import static io.github.kaktushose.jdac.configuration.PropertyType.Enumeration;
import static io.github.kaktushose.jdac.configuration.PropertyType.FallbackBehaviour.ACCUMULATE;
import static io.github.kaktushose.jdac.configuration.PropertyType.FallbackBehaviour.OVERRIDE;
import static io.github.kaktushose.jdac.configuration.PropertyType.Instance;

public class PropertyTypes {

    public static final PropertyType<Collection<ClassFinder>> CLASS_FINDER =
            new Enumeration<>("CLASS_FINDER", ClassFinder.class, OVERRIDE);

    public static final PropertyType<Collection<EmojiSource>> EMOJI_SOURCES =
            new Enumeration<>("EMOJI_SOURCES", EmojiSource.class, OVERRIDE);

    public static final PropertyType<Descriptor> DESCRIPTOR =
            new Instance<>("DESCRIPTOR", Descriptor.class);

    public static final PropertyType<Embeds.Configuration> EMBED_CONFIG =
            new Instance<>("EMEBED_CONFIG", Embeds.Configuration.class);

    public static final PropertyType<Localizer> LOCALIZER =
            new Instance<>("LOCALIZER", Localizer.class);

    public static final PropertyType<InteractionControllerInstantiator> INTERACTION_CONTROLLER_INSTANTIATOR =
            new Instance<>("INTERACTION_CONTROLLER_INSTANTIATOR", InteractionControllerInstantiator.class);

    public static final PropertyType<Collection<Map.Entry<Priority, Middleware>>> MIDDLEWARE =
            new Enumeration<>("MIDDLEWARE", castUnsafe(Map.Entry.class), ACCUMULATE);

    public static final PropertyType<Map<Map.Entry<Type<?>, Type<?>>, TypeAdapter<?, ?>>> TYPE_ADAPTER =
            new Mapping<>("TYPE_ADAPTER", castUnsafe(Map.Entry.class), castUnsafe(TypeAdapter.class), ACCUMULATE);

    public static final PropertyType<Map<Class<? extends Annotation>, Validator<?, ?>>> VALIDATOR =
            new Mapping<>("VALIDATOR", castUnsafe(Class.class), castUnsafe(Validator.class), ACCUMULATE);

    public static final PropertyType<PermissionsProvider> PERMISSION_PROVIDER =
            new Instance<>("PERMISSION_PROVIDER", PermissionsProvider.class);

    public static final PropertyType<ErrorMessageFactory> ERROR_MESSAGE_FACTORY =
            new Instance<>("ERROR_MESSAGE_FACTORY", ErrorMessageFactory.class);

    public static final PropertyType<GuildScopeProvider> GUILD_SCOPE_PROVIDER =
            new Instance<>("GUILD_SCOPE_PROVIDER", GuildScopeProvider.class);

    public static final PropertyType<InteractionDefinition.ReplyConfig> GLOBAL_REPLY_CONFIG =
            new Instance<>("GLOBAL_REPLY_CONFIG", InteractionDefinition.ReplyConfig.class);

    public static final PropertyType<CommandDefinition.CommandConfig> GLOBAL_COMMAND_CONFIG =
            new Instance<>("GLOBAL_COMMAND_CONFIG", CommandDefinition.CommandConfig.class);


    /// only user settable
    ///
    public static final PropertyType<ExtensionFilter> EXTENSION_FILTER =
            new Instance<>("EXTENSION_FILTER", ExtensionFilter.class);

    public static final PropertyType<Collection<String>> PACKAGES =
            new Enumeration<>("PACKAGES", String.class, ACCUMULATE);

    public static final PropertyType<ExpirationStrategy> EXPIRATION_STRATEGY =
            new Instance<>("EXPIRATION_STRATEGY", ExpirationStrategy.class);

    public static final PropertyType<Boolean> LOCALIZE_COMMANDS =
            new Instance<>("LOCALIZE_COMMANDS", Boolean.class);

    public static final PropertyType<Boolean> SHUTDOWN_JDA =
            new Instance<>("SHUTDOWN_JDA", Boolean.class);

    public static final PropertyType<Map<Class<? extends Extension.Data>, Extension.Data>> EXTENSION_DATA =
            new Mapping<>("EXTENSION_DATA", castUnsafe(Class.class), Extension.Data.class, ACCUMULATE);

    /// only created
    public static final PropertyType<I18n> I18N =
            new Instance<>("I18N", I18n.class);

    public static final PropertyType<MessageResolver> MESSAGE_RESOLVER =
            new Instance<>("MESSAGE_RESOLVER", MessageResolver.class);

    public static final PropertyType<EmojiResolver> EMOJI_RESOLVER =
            new Instance<>("EMOJI_RESOLVER", EmojiResolver.class);

    public static final PropertyType<JDAContext> JDA_CONTEXT =
            new Instance<>("JDA_CONTEXT", JDAContext.class);

    public static final PropertyType<Embeds> EMBEDS =
            new Instance<>("EMBEDS", Embeds.class);



    @SuppressWarnings("unchecked")
    private static <T> Class<T> castUnsafe(Class<?> cast) {
        return (Class<T>) cast;
    }
}
