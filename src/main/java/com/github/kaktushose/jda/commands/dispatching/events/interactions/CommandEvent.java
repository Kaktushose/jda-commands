package com.github.kaktushose.jda.commands.dispatching.events.interactions;

import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.Event;
import com.github.kaktushose.jda.commands.dispatching.events.ModalReplyableEvent;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.EphemeralInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

/**
 * This class is a subclass of {@link Event}.
 * It provides additional features for replying to a
 * {@link GenericCommandInteractionEvent GenericCommandInteractionEvent}.
 *
 * @see Event
 * @since 4.0.0
 */
public final class CommandEvent extends ModalReplyableEvent<GenericCommandInteractionEvent> {

    public CommandEvent(GenericCommandInteractionEvent event,
                        InteractionRegistry interactionRegistry,
                        Runtime runtime,
                        EphemeralInteractionDefinition definition) {
        super(event, interactionRegistry, runtime, definition);
    }


    public <T extends GenericCommandInteractionEvent> T jdaEvent(Class<T> type) {
        return type.cast(event);
    }

}
