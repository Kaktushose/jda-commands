package com.github.kaktushose.jda.commands.dispatching.context.internal;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.dispatching.HolyGrail;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;

import java.util.Locale;

public record RichInvocationContext(
        InvocationContext<?> invocationContext,
        Runtime runtime,
        InteractionDefinition.ReplyConfig specificReplyConfig

) {
    public static RichInvocationContext getRichContext() {
        return EventHandler.RICH_INVOCATION_CONTEXT.get();
    }

    public static InvocationContext<?> getInvocationContext() {
        return getRichContext().invocationContext();
    }

    public static Runtime getRuntime() {
        return getRichContext().runtime();
    }

    public static HolyGrail getHolyGrail() {
        return getRuntime().holyGrail();
    }

    public static Locale getUserLocale() {
        return getInvocationContext().event().getUserLocale().toLocale();
    }
}
