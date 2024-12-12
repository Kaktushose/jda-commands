package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;

public abstract sealed class ModalReplyableEvent<T extends GenericInteractionCreateEvent> extends ReplyableEvent<T>
        permits CommandEvent, ComponentEvent {

    protected ModalReplyableEvent(T event, InteractionRegistry interactionRegistry, Runtime runtime, boolean ephemeral) {
        super(event, interactionRegistry, runtime, ephemeral);
    }

    public void replyModal(String modal) {
        IModalCallback callback;
        if (event instanceof IModalCallback) {
            callback = (IModalCallback) event;
        } else {
            throw new IllegalArgumentException(
                    String.format("Cannot reply to '%s'! Please report this error to the jda-commands devs!", event.getClass().getName())
            );
        }

//        ModalDefinition modalDefinition = interactionRegistry().getModals().stream()
//                .filter(it ->
//                        it.getDefinitionId().equals(String.format("%s%s",
//                                context.definition().getMethod().getDeclaringClass().getSimpleName(), modal)))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("Unknown Modal"));
//
//        callback.replyModal(modalDefinition.toModal(runtimeId())).queue();
    }

}
