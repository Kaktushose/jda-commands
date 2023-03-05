package com.github.kaktushose.jda.commands.dispatching.sender.impl;

import com.github.kaktushose.jda.commands.dispatching.sender.ReplyCallback;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Implementation of {@link ReplyCallback} that can handle interaction events. More formally, this callback can handle
 * any event that is a subtype of {@link IReplyCallback}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see ReplyCallback
 * @since 2.3.0
 */
public class InteractionReplyCallback implements ReplyCallback {

    private final IReplyCallback event;
    private final Collection<ItemComponent> actionRows;

    /**
     * Constructs a new {@link ReplyCallback}.
     *
     * @param event      the corresponding event
     * @param actionRows a {@link Collection} of {@link ActionRow ActionRows to send}
     */
    public InteractionReplyCallback(@NotNull IReplyCallback event, @NotNull Collection<ItemComponent> actionRows) {
        this.event = event;
        this.actionRows = actionRows;
    }

    @Override
    public void sendMessage(@NotNull String message, boolean ephemeral, @Nullable Consumer<Message> success) {
        initialReply(ephemeral, hook -> hook.sendMessage(message).queue(success));
    }

    @Override
    public void sendMessage(@NotNull Message message, boolean ephemeral, @Nullable Consumer<Message> success) {
        throw new IllegalStateException();
    }

    @Override
    public void sendMessage(@NotNull MessageEmbed embed, boolean ephemeral, @Nullable Consumer<Message> success) {
        initialReply(ephemeral, hook -> hook.sendMessageEmbeds(embed).addActionRow(actionRows).queue(success));
    }

    @Override
    public void deferReply(boolean ephemeral) {
        event.deferReply(ephemeral).queue();
    }

    private void initialReply(boolean ephemeral, Consumer<InteractionHook> consumer) {
        if (!event.isAcknowledged()) {
            event.deferReply().setEphemeral(ephemeral).queue(consumer);
            return;
        }
        consumer.accept(event.getHook().setEphemeral(ephemeral));
    }
}
