package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
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
        Context context = getContext();
        GenericInteractionCreateEvent event = context.getEvent();
        if (event instanceof IModalCallback) {
            callback = (IModalCallback) event;
        } else {
            throw new IllegalArgumentException(
                    String.format("Cannot reply to '%s'! Please report this error to the jda-commands devs!", event.getClass().getName())
            );
        }

        String id = String.format("%s.%s", context.getInteractionDefinition().getMethod().getDeclaringClass().getSimpleName(), modal);

        ModalDefinition modalDefinition = context.getJdaCommands().getInteractionRegistry().getModals().stream()
                .filter(it -> it.getId().equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Modal"));

        callback.replyModal(modalDefinition.toModal(modalDefinition.getRuntimeId(context))).queue();
    }
}
