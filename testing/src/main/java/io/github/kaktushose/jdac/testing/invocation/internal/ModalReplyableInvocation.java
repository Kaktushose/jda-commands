package io.github.kaktushose.jdac.testing.invocation.internal;

import io.github.kaktushose.jdac.testing.TestScenario;
import io.github.kaktushose.jdac.testing.invocation.commands.SlashCommandInvocation;
import io.github.kaktushose.jdac.testing.reply.ModalEventReply;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract sealed class ModalReplyableInvocation<T extends IReplyCallback> extends Invocation<T>
        permits SlashCommandInvocation, ComponentInvocation {

    protected final CompletableFuture<Modal> modal;

    public ModalReplyableInvocation(TestScenario.Context context, Class<T> eventClass, InteractionType interactionType) {
        super(context, eventClass, interactionType);
        modal = new CompletableFuture<>();
    }

    /// Used when an event is replied to with a Modal.
    public ModalEventReply invokeModal() {
        context.eventManager().handle((GenericInteractionCreateEvent) event);
        try {
            return new ModalEventReply(this, context, modal.get(5, TimeUnit.SECONDS));
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
