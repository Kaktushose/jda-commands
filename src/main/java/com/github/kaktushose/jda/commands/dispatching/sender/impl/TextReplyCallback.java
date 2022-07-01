package com.github.kaktushose.jda.commands.dispatching.sender.impl;

import com.github.kaktushose.jda.commands.dispatching.sender.ReplyCallback;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Implementation of {@link ReplyCallback} that can handle any type of event. More formally, this callback can handle
 * any event that has a {@link MessageChannel} linked to it.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see ReplyCallback
 * @see InteractionReplyCallback
 * @since 2.3.0
 */
public class TextReplyCallback implements ReplyCallback {

    private final MessageChannel channel;
    private final Collection<ActionRow> actionRows;

    /**
     * Constructs a new {@link ReplyCallback}.
     *
     * @param channel    the corresponding {@link TextChannel}
     * @param actionRows a {@link Collection} of {@link ActionRow ActionRows to send}
     */
    public TextReplyCallback(MessageChannel channel, Collection<ActionRow> actionRows) {
        this.channel = channel;
        this.actionRows = actionRows;
    }

    @Override
    public void sendMessage(@NotNull String message, boolean ephemeral, @Nullable Consumer<Message> success) {
        channel.sendMessage(message).setActionRows(actionRows).queue(success);
    }

    @Override
    public void sendMessage(@NotNull Message message, boolean ephemeral, @Nullable Consumer<Message> success) {
        channel.sendMessage(message).setActionRows(actionRows).queue(success);
    }

    @Override
    public void sendMessage(@NotNull MessageEmbed embed, boolean ephemeral, @Nullable Consumer<Message> success) {
        channel.sendMessageEmbeds(embed).setActionRows(actionRows).queue(success);
    }

}
