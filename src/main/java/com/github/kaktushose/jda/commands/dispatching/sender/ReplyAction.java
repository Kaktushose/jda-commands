package com.github.kaktushose.jda.commands.dispatching.sender;

import com.github.kaktushose.jda.commands.embeds.EmbedDTO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Generic interface holding reply methods. Uses a {@link ReplyCallback} to send the replies.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see EditAction
 * @see ReplyCallback
 * @since 2.3.0
 */
public interface ReplyAction {

    /**
     * Sends a message to the TextChannel where the button was called.
     *
     * @param message the message to send
     */
    default void reply(@NotNull String message) {
        reply(message, isEphemeral(), (Consumer<Message>) null);
    }

    /**
     * Sends a formatted message using the specified format string and arguments to the TextChannel where the button was called.
     *
     * @param format the message to send
     * @param args   Arguments referenced by the format specifiers in the format string. If there are more arguments than
     *               format specifiers, the extra arguments are ignored. The number of arguments is variable and may be
     *               zero.
     * @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format specifier that
     *                                          is incompatible with the given arguments, insufficient arguments given
     *                                          the format string, or other illegal conditions.
     */
    default void reply(@NotNull String format, @NotNull Object... args) {
        reply(String.format(format, args), isEphemeral());
    }

    /**
     * Sends a message to the TextChannel where the button was called.
     *
     * @param message the {@code Message} to send
     */
    default void reply(@NotNull Message message) {
        reply(message, isEphemeral(), null);
    }

    /**
     * Sends a message to the TextChannel where the button was called.
     *
     * @param builder the {@code MessageBuilder} to send
     */
    default void reply(@NotNull MessageBuilder builder) {
        reply(builder, isEphemeral(), null);
    }


    /**
     * Sends a message to the TextChannel where the button was called.
     *
     * @param builder the {@code EmbedBuilder} to send
     */
    default void reply(@NotNull EmbedBuilder builder) {
        reply(builder, isEphemeral(), null);
    }

    /**
     * Sends a message to the TextChannel where the button was called.
     *
     * @param embedDTO the {@link EmbedDTO} to send
     */
    default void reply(@NotNull EmbedDTO embedDTO) {
        reply(embedDTO, isEphemeral(), null);
    }

    /**
     * Sends a message to the TextChannel where the button was called.
     *
     * @param message the message to send
     */
    default void reply(@NotNull String message, boolean ephemeral) {
        reply(message, ephemeral, (Consumer<Message>) null);
    }

    /**
     * Sends a formatted message using the specified format string and arguments to the TextChannel where the button was called.
     *
     * @param format    the message to send
     * @param ephemeral whether to send an ephemeral reply
     * @param args      Arguments referenced by the format specifiers in the format string. If there are more arguments than
     *                  format specifiers, the extra arguments are ignored. The number of arguments is variable and may be
     *                  zero.
     * @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format specifier that
     *                                          is incompatible with the given arguments, insufficient arguments given
     *                                          the format string, or other illegal conditions.
     */
    default void reply(@NotNull String format, boolean ephemeral, @NotNull Object... args) {
        reply(String.format(format, args), ephemeral);
    }

    /**
     * Sends a message to the TextChannel where the button was called.
     *
     * @param message   the {@code Message} to send
     * @param ephemeral whether to send an ephemeral reply
     */
    default void reply(@NotNull Message message, boolean ephemeral) {
        reply(message, ephemeral, null);
    }

    /**
     * Sends a message to the TextChannel where the button was called.
     *
     * @param builder   the {@code MessageBuilder} to send
     * @param ephemeral whether to send an ephemeral reply
     */
    default void reply(@NotNull MessageBuilder builder, boolean ephemeral) {
        reply(builder, ephemeral, null);
    }


    /**
     * Sends a message to the TextChannel where the button was called.
     *
     * @param builder   the {@code EmbedBuilder} to send
     * @param ephemeral whether to send an ephemeral reply
     */
    default void reply(@NotNull EmbedBuilder builder, boolean ephemeral) {
        reply(builder, ephemeral, null);
    }

    /**
     * Sends a message to the TextChannel where the button was called.
     *
     * @param embedDTO  the {@link EmbedDTO} to send
     * @param ephemeral whether to send an ephemeral reply
     */
    default void reply(@NotNull EmbedDTO embedDTO, boolean ephemeral) {
        reply(embedDTO, ephemeral, null);
    }

    /**
     * Sends a message to the TextChannel where the button was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message the {@link String} message to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void reply(@NotNull String message, @Nullable Consumer<Message> success) {
        reply(message, isEphemeral(), success);
    }

    /**
     * Sends a message to the TextChannel where the button was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message the {@link Message} to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void reply(@NotNull Message message, @Nullable Consumer<Message> success) {
        reply(message, isEphemeral(), success);
    }

    /**
     * Sends a message to the TextChannel where the button was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param builder the {@link EmbedBuilder} to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void reply(@NotNull EmbedBuilder builder, @Nullable Consumer<Message> success) {
        reply(builder, isEphemeral(), success);
    }

    /**
     * Sends a message to the TextChannel where the button was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param builder the {@link MessageBuilder} to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void reply(@NotNull MessageBuilder builder, @Nullable Consumer<Message> success) {
        reply(builder, isEphemeral(), success);
    }

    /**
     * Sends a message to the TextChannel where the button was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param embedDTO the {@link EmbedDTO} to send
     * @param success  the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void reply(@NotNull EmbedDTO embedDTO, @Nullable Consumer<Message> success) {
        reply(embedDTO, isEphemeral(), success);
    }

    /**
     * Sends a message to the TextChannel where the button was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message   the {@link String} message to send
     * @param ephemeral whether to send an ephemeral reply
     * @param success   the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void reply(@NotNull String message, boolean ephemeral, @Nullable Consumer<Message> success) {
        getReplyCallback().sendMessage(message, ephemeral, success);
    }

    /**
     * Sends a message to the TextChannel where the button was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message   the {@link Message} to send
     * @param ephemeral whether to send an ephemeral reply
     * @param success   the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void reply(@NotNull Message message, boolean ephemeral, @Nullable Consumer<Message> success) {
        getReplyCallback().sendMessage(message, ephemeral, success);
    }

    /**
     * Sends a message to the TextChannel where the button was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param builder   the {@link EmbedBuilder} to send
     * @param ephemeral whether to send an ephemeral reply
     * @param success   the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void reply(@NotNull EmbedBuilder builder, boolean ephemeral, @Nullable Consumer<Message> success) {
        getReplyCallback().sendMessage(builder, ephemeral, success);
    }

    /**
     * Sends a message to the TextChannel where the button was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param builder   the {@link MessageBuilder} to send
     * @param ephemeral whether to send an ephemeral reply
     * @param success   the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void reply(@NotNull MessageBuilder builder, boolean ephemeral, @Nullable Consumer<Message> success) {
        getReplyCallback().sendMessage(builder, ephemeral, success);
    }

    /**
     * Sends a message to the TextChannel where the button was called. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param embedDTO  the {@link EmbedDTO} to send
     * @param ephemeral whether to send an ephemeral reply
     * @param success   the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void reply(@NotNull EmbedDTO embedDTO, boolean ephemeral, @Nullable Consumer<Message> success) {
        getReplyCallback().sendMessage(embedDTO, ephemeral, success);
    }

    /**
     * Adds an {@link ActionRow} to the reply and adds the passed buttons to it. The buttons must be defined in the same
     * {@link com.github.kaktushose.jda.commands.annotations.CommandController CommandController} as the referring
     * {@link com.github.kaktushose.jda.commands.annotations.Command Command}.
     *
     * @param buttons the ids of the buttons to add
     * @return the current instance for fluent interface
     */
    ReplyAction withButtons(@NotNull String... buttons);

    /**
     * Deletes the original message the button was attached to.
     */
    default void deleteOriginal(boolean ephemeral) {
        getReplyCallback().deleteOriginal(ephemeral);
    }

    /**
     * Gets the {@link ReplyCallback} to use.
     *
     * @return the {@link ReplyCallback}
     */
    @NotNull
    ReplyCallback getReplyCallback();

    /**
     * Whether to reply ephemeral.
     *
     * @return {@code true} if replies should be ephemeral
     */
    boolean isEphemeral();

}
