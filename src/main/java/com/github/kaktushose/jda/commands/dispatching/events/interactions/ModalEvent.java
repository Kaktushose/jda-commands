package com.github.kaktushose.jda.commands.dispatching.events.interactions;

import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.Event;
import com.github.kaktushose.jda.commands.dispatching.events.ReplyableEvent;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.ReplyConfig;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

/**
 * This class is a subclass of {@link Event}.
 * It provides additional features for replying to a
 * {@link net.dv8tion.jda.api.events.interaction.ModalInteractionEvent ModalInteractionEvent}.
 *
 * @see Event
 * @since 4.0.0
 */
public final class ModalEvent extends ReplyableEvent<ModalInteractionEvent> {

    public ModalEvent(ModalInteractionEvent event,
                      InteractionRegistry interactionRegistry,
                      Runtime runtime,
                      ReplyConfig replyConfig) {
        super(event, interactionRegistry, runtime, replyConfig);
    }

}
