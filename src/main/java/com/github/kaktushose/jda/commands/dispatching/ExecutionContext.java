package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.dispatching.handling.HandlerContext;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.EphemeralInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExecutionContext<T extends GenericInteractionCreateEvent, U extends GenericInteractionDefinition> {
    private final T event;
    private final U definition;
    private final Runtime runtime;
    private final HandlerContext handlerContext;
    private final boolean ephemeral;
    private final List<Object> arguments;
    private boolean cancelled;
    private MessageCreateData errorMessage;

    public ExecutionContext(T event, U definition, Runtime runtime, HandlerContext handlerContext, List<Object> arguments) {
        this.event = event;
        this.definition = definition;
        this.runtime = runtime;
        this.handlerContext = handlerContext;
        this.ephemeral = definition instanceof EphemeralInteractionDefinition ep && ep.isEphemeral();
        this.arguments = arguments;
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

    public List<Object> arguments() {
        return arguments;
    }
}
