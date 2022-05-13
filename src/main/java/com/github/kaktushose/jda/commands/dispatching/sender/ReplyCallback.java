package com.github.kaktushose.jda.commands.dispatching.sender;

import com.github.kaktushose.jda.commands.embeds.EmbedDTO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Generic reply callback used to reply to {@link com.github.kaktushose.jda.commands.dispatching.CommandEvent CommandEvents}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see com.github.kaktushose.jda.commands.dispatching.sender.impl.TextReplyCallback TextReplyCallback
 * @see com.github.kaktushose.jda.commands.dispatching.sender.impl.InteractionReplyCallback InteractionReplyCallback
 * @since 2.3.0
 */
public interface ReplyCallback {

    /**
     * Sends a message to the TextChannel where the command was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message the {@link String} message to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    void sendMessage(@NotNull String message, @Nullable Consumer<Message> success);

    /**
     * Sends a message to the TextChannel where the command was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message the {@link Message} to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    void sendMessage(@NotNull Message message, @Nullable Consumer<Message> success);

    /**
     * Sends a message to the TextChannel where the command was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param embed   the {@link MessageEmbed} to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    void sendMessage(@NotNull MessageEmbed embed, @Nullable Consumer<Message> success);

    /**
     * Sends a message to the TextChannel where the command was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param builder the {@link EmbedBuilder} to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void sendMessage(@NotNull EmbedBuilder builder, @Nullable Consumer<Message> success) {
        sendMessage(builder.build(), success);
    }

    /**
     * Sends a message to the TextChannel where the command was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param builder the {@link MessageBuilder} to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void sendMessage(@NotNull MessageBuilder builder, @Nullable Consumer<Message> success) {
        sendMessage(builder.build(), success);
    }

    /**
     * Sends a message to the TextChannel where the command was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param embedDTO the {@link EmbedDTO} to send
     * @param success  the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void sendMessage(@NotNull EmbedDTO embedDTO, @Nullable Consumer<Message> success) {
        sendMessage(embedDTO.toMessageEmbed(), success);
    }

}
