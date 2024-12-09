package com.github.kaktushose.jda.commands.dispatching.refactor;

import com.github.kaktushose.jda.commands.dispatching.refactor.handling.HandlerContext;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.Nullable;

public final class ExecutionContext<T extends GenericInteractionCreateEvent, U extends GenericInteractionDefinition> {

    private final T event;
    private final U definition;
    private final Runtime runtime;
    private final HandlerContext handlerContext;
    private boolean cancelled;
    private MessageCreateData errorMessage;
    private boolean ephemeral;

    public ExecutionContext(T event, U definition, Runtime runtime, HandlerContext handlerContext) {
        this.event = event;
        this.definition = definition;
        this.runtime = runtime;
        this.handlerContext = handlerContext;
    }

    public T event() {
        return event;
    }

    @Nullable
    public MessageCreateData errorMessage() {
        return errorMessage;
    }

    public boolean cancelled() {
        return cancelled;
    }

    public void cancel(MessageCreateData errorMessage) {
        this.cancelled = true;
        this.errorMessage = errorMessage;
    }

    public void uncancel() {
        this.cancelled = false;
    }

    public boolean ephemeral() {
        return ephemeral;
    }

    public void ephemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
    }

    public Runtime runtime() {
        return runtime;
    }

    public U interactionDefinition() {
        return definition;
    }

    public ImplementationRegistry implementationRegistry() {
        return handlerContext.implementationRegistry();
    }

    public InteractionRegistry interactionRegistry() {
        return handlerContext.interactionRegistry();
    }
}
