package com.github.kaktushose.jda.commands.dispatching.sender.impl;

import com.github.kaktushose.jda.commands.dispatching.sender.ReplyCallback;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Implementation of {@link ReplyCallback} used for {@link SlashCommandInteractionEvent SlashCommandInteractionEvents}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see ReplyCallback
 * @see InteractionReplyCallback
 * @since 2.3.0
 */
public class TextReplyCallback implements ReplyCallback {

    private final TextChannel channel;

    /**
     * Constructs a new {@link ReplyCallback}.
     *
     * @param channel the corresponding {@link TextChannel}
     */
    public TextReplyCallback(TextChannel channel) {
        this.channel = channel;
    }

    @Override
    public void sendMessage(@NotNull String message, @Nullable Consumer<Message> success) {
        channel.sendMessage(message).queue(success);
    }

    @Override
    public void sendMessage(@NotNull Message message, @Nullable Consumer<Message> success) {
        channel.sendMessage(message).queue(success);

    }

    @Override
    public void sendMessage(@NotNull MessageEmbed embed, @Nullable Consumer<Message> success) {
        channel.sendMessageEmbeds(embed).queue();
    }

}
