package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ModalEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.ConfigurableReply;
import com.github.kaktushose.jda.commands.dispatching.reply.MessageReply;
import com.github.kaktushose.jda.commands.dispatching.reply.Reply;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.ReplyConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

public sealed abstract class ReplyableEvent<T extends GenericInteractionCreateEvent> extends Event<T> implements Reply
        permits ModalEvent, ModalReplyableEvent {

    private final ReplyConfig replyConfig;

    protected ReplyableEvent(T event,
                             InteractionRegistry interactionRegistry,
                             Runtime runtime,
                             ReplyConfig replyConfig) {
        super(event, interactionRegistry, runtime);
        this.replyConfig = replyConfig;
    }

    /**
     * Removes all components from the last message that was sent.
     */
    public void removeComponents() {
        if (event instanceof IReplyCallback callback) {
            if (!event.isAcknowledged()) {
                callback.deferReply(replyConfig.ephemeral()).queue();
            }
            callback.getHook().editOriginalComponents().queue();
        }
    }

    private MessageReply newReply() {
        return new MessageReply(event, replyConfig);
    }

    public ConfigurableReply with() {
        return new ConfigurableReply(newReply(), interactionRegistry, runtime);
    }

    public Message reply(@NotNull String message) {
        return newReply().reply(message);
    }

    public Message reply(@NotNull MessageCreateData message) {
        return newReply().reply(message);
    }

    public Message reply(@NotNull EmbedBuilder builder) {
        return newReply().reply(builder);
    }
}
