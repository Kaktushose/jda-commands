package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.dispatching.InvocationContext;
import com.github.kaktushose.jda.commands.reflect.interactions.ModalDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;

/**
 * Generic interface holding reply methods for modal replies.
 *
 * @since 4.0.0
 */
public interface ModalReplyable extends Replyable {

    /**
     * Replies to this Interaction with a Modal. This will open a popup on the target user's Discord client.
     *
     * @param modal the id of the modal to reply with
     */
    default void replyModal(String modal) {
        IModalCallback callback;
        InvocationContext<?> context = getContext();
        GenericInteractionCreateEvent event = context.event();
        if (event instanceof IModalCallback) {
            callback = (IModalCallback) event;
        } else {
            throw new IllegalArgumentException(
                    String.format("Cannot reply to '%s'! Please report this error to the jda-commands devs!", event.getClass().getName())
            );
        }

        ModalDefinition modalDefinition = context.interactionRegistry().getModals().stream()
                .filter(it -> it.getDefinitionId().equals(String.format("%s%s", context.definition().getMethod().getDeclaringClass().getSimpleName(), modal)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Modal"));

        callback.replyModal(modalDefinition.toModal(context.runtimeId())).queue();
    }
}
