package com.github.kaktushose.jda.commands.dispatching.context.internal;

import com.github.kaktushose.jda.commands.dispatching.HolyGrail;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;

import java.util.Locale;

public record RichInvocationContext(
        InvocationContext<?> invocationContext,
        Runtime runtime

) {
    public static InvocationContext<?> getContext() {
        return EventHandler.RICH_INVOCATION_CONTEXT.get().invocationContext();
    }

    public static Runtime getRuntime() {
        return EventHandler.RICH_INVOCATION_CONTEXT.get().runtime();
    }

    public static HolyGrail getHolyGrail() {
        return getRuntime().holyGrail();
    }

    public static Locale getUserLocale() {
        return getContext().event().getUserLocale().toLocale();
    }
}
