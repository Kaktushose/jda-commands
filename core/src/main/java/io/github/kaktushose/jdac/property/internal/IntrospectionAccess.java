package io.github.kaktushose.jdac.property.internal;

import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import io.github.kaktushose.jdac.property.JDACProperty;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;

@ApiStatus.Internal
public class IntrospectionAccess {
    public static InvocationContext<?> scopedInvocationContext() {
        return JDACProperty.INVOCATION_CONTEXT.scopedGet();
    }

    public static Runtime scopedRuntime() {
        return JDACInternalProperties.RUNTIME.scopedGet();
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
        return JDACInternalProperties.INTERACTION_REGISTRY.scopedGet();
    }

    public static Embeds scopedEmbeds() {
        return JDACInternalProperties.EMBEDS.scopedGet();
    }

    public static MessageResolver scopedMessageResolver() {
        return JDACProperty.MESSAGE_RESOLVER.scopedGet();
    }

    public static I18n scopedI18n() {
        return JDACProperty.I18N.scopedGet();
    }
}
