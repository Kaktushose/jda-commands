package io.github.kaktushose.jdac.introspection.internal;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.internal.InternalProperties;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.message.MessageResolver;
import io.github.kaktushose.jdac.message.i18n.I18n;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;

import static io.github.kaktushose.jdac.introspection.Introspection.scopedGet;

@ApiStatus.Internal
public class IntrospectionAccess {
    public static InvocationContext<?> scopedInvocationContext() {
        return scopedGet(Property.INVOCATION_CONTEXT);
    }

    public static Runtime scopedRuntime() {
        return scopedGet(InternalProperties.RUNTIME);
    }

    public static Locale scopedUserLocale() {
        return scopedInvocationContext().event().getUserLocale().toLocale();
    }

    public static GenericInteractionCreateEvent scopedJdaEvent() {
        return scopedInvocationContext().event();
    }

    public static InteractionDefinition.ReplyConfig scopedReplyConfig() {
        return scopedInvocationContext().replyConfig();
    }

    public static InteractionRegistry scopedInteractionRegistry() {
        return scopedGet(InternalProperties.INTERACTION_REGISTRY);
    }

    public static Embeds scopedEmbeds() {
        return scopedGet(InternalProperties.EMBEDS);
    }

    public static MessageResolver scopedMessageResolver() {
        return scopedGet(Property.MESSAGE_RESOLVER);
    }

    public static I18n scopedI18n() {
        return scopedGet(Property.I18N);
    }
}
