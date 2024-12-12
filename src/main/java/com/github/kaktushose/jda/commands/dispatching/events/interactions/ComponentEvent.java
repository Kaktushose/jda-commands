package com.github.kaktushose.jda.commands.dispatching.events.interactions;

import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.Event;
import com.github.kaktushose.jda.commands.dispatching.events.ModalReplyableEvent;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;

/**
 * This class is a subclass of {@link Event}.
 * It provides additional features for replying to a
 * {@link net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent GenericComponentInteractionCreateEvent}.
 *
 * @see Event
 * @since 4.0.0
 */
public final class ComponentEvent extends ModalReplyableEvent<GenericComponentInteractionCreateEvent> {

    public ComponentEvent(GenericComponentInteractionCreateEvent event,
                          InteractionRegistry interactionRegistry,
                          Runtime runtime,
                          boolean ephemeral) {
        super(event, interactionRegistry, runtime, ephemeral);
    }

    public <T extends GenericComponentInteractionCreateEvent> T jdaEvent(Class<T> type) {
        return type.cast(event);
    }

    @Override
    protected void queue() {
        runtime.latestReply().filter(_ ->
                replyBuilder.keepComponents() && replyBuilder.messageCreateBuilder().getComponents().isEmpty()
        ).ifPresent(it -> replyBuilder.messageCreateBuilder().setComponents(it.getComponents()));

        replyBuilder.queue();
        runtime.latestReply(replyBuilder.toMessageCreateData());
    }
}
