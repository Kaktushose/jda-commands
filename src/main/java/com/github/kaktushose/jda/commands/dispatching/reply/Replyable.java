package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.components.Buttons;
import com.github.kaktushose.jda.commands.components.Component;
import com.github.kaktushose.jda.commands.data.EmbedDTO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Generic interface holding reply methods.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 2.3.0
 */
public interface Replyable {

    /**
     * A no-op consumer used as a placeholder.
     */
    Consumer<Message> EMPTY_CONSUMER = unused -> {};

    /**
     * Sends a message to the TextChannel where the interaction was executed.
     *
     * @param message the message to send
     */
    default void reply(@NotNull String message) {
        getReplyContext().getBuilder().setContent(message);
        setConsumer(EMPTY_CONSUMER);
        reply();
    }

    /**
     * Sends a formatted message using the specified format string and arguments to the TextChannel where the interaction was executed.
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
        getReplyContext().getBuilder().setContent(String.format(format, args));
        setConsumer(EMPTY_CONSUMER);
        reply();
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed.
     *
     * @param message the {@code Message} to send
     */
    default void reply(@NotNull MessageCreateData message) {
        getReplyContext().getBuilder().applyData(message);
        setConsumer(EMPTY_CONSUMER);
        reply();
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed.
     *
     * @param builder the {@code EmbedBuilder} to send
     */
    default void reply(@NotNull EmbedBuilder builder) {
        getReplyContext().getBuilder().setEmbeds(builder.build());
        setConsumer(EMPTY_CONSUMER);
        reply();
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed.
     *
     * @param embedDTO the {@link EmbedDTO} to send
     */
    default void reply(@NotNull EmbedDTO embedDTO) {
        getReplyContext().getBuilder().applyData(embedDTO.toMessageCreateData());
        setConsumer(EMPTY_CONSUMER);
        reply();
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message the {@link String} message to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void reply(@NotNull String message, @Nullable Consumer<Message> success) {
        reply(message);
        setConsumer(success);
        reply();
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message the {@link Message} to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void reply(@NotNull MessageCreateData message, @Nullable Consumer<Message> success) {
        reply(message);
        setConsumer(success);
        reply();
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param builder the {@link EmbedBuilder} to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void reply(@NotNull EmbedBuilder builder, @Nullable Consumer<Message> success) {
        reply(builder);
        setConsumer(success);
        reply();
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param embedDTO the {@link EmbedDTO} to send
     * @param success  the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void reply(@NotNull EmbedDTO embedDTO, @Nullable Consumer<Message> success) {
        reply(embedDTO);
        setConsumer(success);
        reply();
    }

    /**
     * Adds an {@link ActionRow} to the reply and adds the passed {@link Component Components} to it.
     * For buttons, they must be defined in the same
     * {@link com.github.kaktushose.jda.commands.annotations.interactions.Interaction Interaction} as the referring
     * {@link SlashCommand Command}.
     *
     * @param components the {@link Component Components} to add
     * @return the current instance for fluent interface
     */
    Replyable with(@NotNull Component... components);

    /**
     * Adds an {@link ActionRow} to the reply and adds the passed {@link Component Components} to it.
     * The buttons must be defined in the same
     * {@link com.github.kaktushose.jda.commands.annotations.interactions.Interaction Interaction} as the referring
     * {@link SlashCommand Command}. This will enable all buttons. To add
     * disabled buttons, use {@link #with(Component...)}.
     *
     * @param buttons the id of the buttons to add
     * @return the current instance for fluent interface
     */
    default Replyable withButtons(@NotNull String... buttons) {
        with(Buttons.enabled(buttons));
        return this;
    }

    /**
     * Whether to send ephemeral replies.
     *
     * @param ephemeral {@code true} if replies should be ephemeral
     * @return the current instance for fluent interface
     */
    default Replyable setEphemeral(boolean ephemeral) {
        getReplyContext().setEphemeralReply(ephemeral);
        return this;
    }

    /**
     * Sets the success callback consumer.
     *
     * @param success the callback consumer
     * @return the current instance for fluent interface
     */
    private Replyable setConsumer(Consumer<Message> success) {
        getReplyContext().setConsumer(success);
        return this;
    }

    /**
     * Whether this reply should edit the existing message or send a new one
     *
     * @param edit {@code true} if this reply should edit the existing message
     * @return the current instance for fluent interface
     */
    default Replyable editReply(boolean edit) {
        getReplyContext().setEditReply(edit);
        return this;
    }

    /**
     * Whether this reply should clear all components that are attached to the previous message
     *
     * @param clear {@code true} if this reply should clear all components
     * @return the current instance for fluent interface
     */
    default Replyable clearComponents(boolean clear) {
        getReplyContext().setClearComponents(clear);
        return this;
    }

    /**
     * Gets the {@link ReplyContext} to use.
     *
     * @return the {@link ReplyContext}
     */
    @NotNull ReplyContext getReplyContext();

    /**
     * Sends a reply with no message content. The main use-case of this method is for editing components of a reply.
     */
    void reply();

}
