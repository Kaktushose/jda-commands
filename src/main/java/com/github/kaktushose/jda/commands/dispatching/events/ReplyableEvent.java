package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ModalEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.ConfigurableReply;
import com.github.kaktushose.jda.commands.dispatching.reply.MessageReply;
import com.github.kaktushose.jda.commands.dispatching.reply.Reply;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

public sealed abstract class ReplyableEvent<T extends GenericInteractionCreateEvent> extends Event<T> implements Reply
        permits ModalEvent, ModalReplyableEvent {

    private final boolean ephemeral;

    protected ReplyableEvent(T event,
                             InteractionRegistry interactionRegistry,
                             Runtime runtime,
                             boolean ephemeral) {
        super(event, interactionRegistry, runtime);
        this.ephemeral = ephemeral;
    }

    /**
     * Removes all components from the last message that was sent.
     */
    public void removeComponents() {
        if (event instanceof IReplyCallback callback) {
            if (!event.isAcknowledged()) {
                callback.deferReply(false).queue();
            }
            callback.getHook().editOriginalComponents().queue();
        }
    }

    private MessageReply newReply() {
        return new MessageReply(event, ephemeral);
    }

    public ConfigurableReply with() {
        return new ConfigurableReply(newReply(), interactionRegistry, runtime);
    }

    public void reply(@NotNull String message) {
        newReply().reply(message);
    }

    public void reply(@NotNull MessageCreateData message) {
        newReply().reply(message);
    }

    public void reply(@NotNull EmbedBuilder builder) {
        newReply().reply(builder);
    }
}
