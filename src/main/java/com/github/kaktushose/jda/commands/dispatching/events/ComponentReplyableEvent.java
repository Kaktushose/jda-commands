package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyBuilder;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import java.util.function.Consumer;

public final class ComponentReplyableEvent<T extends GenericInteractionCreateEvent> extends ReplyableEvent<T> {

    private ComponentReplyableEvent(T event,
                                    InteractionRegistry interactionRegistry,
                                    Runtime runtime,
                                    ReplyBuilder replyBuilder) {
        super(event, interactionRegistry, runtime, replyBuilder);
    }

    public static <E extends GenericInteractionCreateEvent> ComponentReplyableEvent<E> fromReplyableEvent(ReplyableEvent<E> replyableEvent) {
        return new ComponentReplyableEvent<>(replyableEvent.event,
                replyableEvent.interactionRegistry,
                replyableEvent.runtime,
                replyableEvent.replyBuilder
        );
    }

    /**
     * Sends the reply to Discord. Used if you only want to edit components or if you have constructed the reply
     * message by using {@link #replyBuilder}.
     */
    public void reply() {
        queue();
    }

    /**
     * Sends the reply to Discord. Used if you only want to edit components or if you have constructed the reply
     * message by using {@link #replyBuilder}. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param success the JDA RestAction success consumer
     */
    public void reply(Consumer<Message> success) {
        replyBuilder.onSuccess(success);
        queue();
    }

    /**
     * Sends the reply to Discord. Used if you only want to edit components or if you have constructed the reply
     * message by using {@link #replyBuilder}. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param success the JDA RestAction success consumer
     * @param failure the JDA RestAction failure consumer
     */
    public void reply(Consumer<Message> success, Consumer<Throwable> failure) {
        replyBuilder.onSuccess(success);
        replyBuilder.onFailure(failure);
        queue();
    }

}
