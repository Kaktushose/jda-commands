package io.github.kaktushose.jdac.dispatching.context.internal;

import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.dispatching.FrameworkContext;
import io.github.kaktushose.jdac.dispatching.Runtime;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.dispatching.handling.EventHandler;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;

@ApiStatus.Internal
public record RichInvocationContext(
        InvocationContext<?> invocationContext,
        Runtime runtime

) {

    public static boolean scopeBound() {
        return EventHandler.RICH_INVOCATION_CONTEXT.isBound();
    }

    public static RichInvocationContext getRichContext() {
        return EventHandler.RICH_INVOCATION_CONTEXT.get();
    }

    public static InvocationContext<?> getInvocationContext() {
        return getRichContext().invocationContext();
    }

    public static Runtime getRuntime() {
        return getRichContext().runtime();
    }

    public static FrameworkContext getFramework() {
        return getRuntime().framework();
    }

    public static Locale getUserLocale() {
        return getInvocationContext().event().getUserLocale().toLocale();
    }

    public static GenericInteractionCreateEvent getJdaEvent() {
        return getInvocationContext().event();
    }

    public static InteractionDefinition.ReplyConfig getReplyConfig() {
        return getInvocationContext().data().replyConfig();
    }
}
