package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.components.Buttons;
import com.github.kaktushose.jda.commands.components.Component;
import com.github.kaktushose.jda.commands.data.EmbedDTO;
import com.github.kaktushose.jda.commands.dispatching.GenericEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Generic interface holding reply methods.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 2.3.0
 */
public interface Replyable {

    Logger log = LoggerFactory.getLogger(GenericEvent.class);

    /**
     * Sends a message to the TextChannel where the interaction was executed.
     *
     * @param message the message to send
     */
    default void reply(@NotNull String message) {
        getReplyContext().getBuilder().setContent(message);
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
        reply();
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed.
     *
     * @param message the {@code Message} to send
     */
    default void reply(@NotNull MessageCreateData message) {
        getReplyContext().getBuilder().applyData(message);
        reply();
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed.
     *
     * @param builder the {@code EmbedBuilder} to send
     */
    default void reply(@NotNull EmbedBuilder builder) {
        getReplyContext().getBuilder().setEmbeds(builder.build());
        reply();
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed.
     *
     * @param embedDTO the {@link EmbedDTO} to send
     */
    default void reply(@NotNull EmbedDTO embedDTO) {
        getReplyContext().getBuilder().applyData(embedDTO.toMessageCreateData());
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
        setSuccessCallback(success);
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
        setSuccessCallback(success);
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
        setSuccessCallback(success);
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
        setSuccessCallback(success);
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
    private Replyable setSuccessCallback(Consumer<Message> success) {
        getReplyContext().setSuccessCallback(success);
        return this;
    }

    /**
     * Sets the failure callback consumer.
     *
     * @param failure the callback consumer
     * @return the current instance for fluent interface
     */
    private Replyable setFailureCallback(Consumer<Throwable> failure) {
        getReplyContext().setFailureCallback(failure);
        return this;
    }

    /**
     * Whether this reply should edit the existing message or send a new one. The default value is
     * {@code true}.
     *
     * @param edit {@code true} if this reply should edit the existing message
     * @return the current instance for fluent interface
     */
    default Replyable editReply(boolean edit) {
        getReplyContext().setEditReply(edit);
        return this;
    }

    /**
     * Whether this reply should keep all components that are attached to the previous message. The default value is
     * {@code true}.
     *
     * @param keep {@code true} if this reply should keep all components
     * @return the current instance for fluent interface
     */
    default Replyable keepComponents(boolean keep) {
        getReplyContext().setKeepComponents(keep);
        return this;
    }

    /**
     * Sends the reply to Discord. Use this if you only want to edit components or if you have constructed the reply
     * message by using {@link #getReplyContext()}. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param success the JDA RestAction success consumer
     */
    default void reply(Consumer<Message> success) {
        setSuccessCallback(success);
        reply();
    }

    /**
     * Sends the reply to Discord. Use this if you only want to edit components or if you have constructed the reply
     * message by using {@link #getReplyContext()}. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param success the JDA RestAction success consumer
     * @param failure the JDA RestAction failure consumer
     */
    default void reply(Consumer<Message> success, Consumer<Throwable> failure) {
        setSuccessCallback(success);
        setFailureCallback(failure);
        reply();
    }

    /**
     * Removes all components from the last message that was sent. <b>This will only work with
     * {@link #editReply(boolean)} set to true.</b>
     */
    default void removeComponents() {
        getReplyContext().removeComponents();
    }

    /**
     * Sends the reply to Discord. Use this if you only want to edit components or if you have constructed the reply
     * message by using {@link #getReplyContext()}.
     */
    void reply();

    /**
     * Gets the {@link ReplyContext} to use.
     *
     * @return the {@link ReplyContext}
     */
    @NotNull ReplyContext getReplyContext();

}
