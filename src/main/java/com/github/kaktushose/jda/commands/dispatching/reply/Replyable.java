package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.embeds.EmbedDTO;
import com.github.kaktushose.jda.commands.interactions.components.Buttons;
import com.github.kaktushose.jda.commands.interactions.components.Component;
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
 * @version 2.3.0
 * @since 2.3.0
 */
public interface Replyable {

    /**
     * Sends a message to the TextChannel where the button was called.
     *
     * @param message the message to send
     */
    default void reply(@NotNull String message) {
       getReplyContext().getBuilder().setContent(message);
       reply();
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
        getReplyContext().getBuilder().setContent(String.format(format, args));
        reply();
    }

    /**
     * Sends a message to the TextChannel where the button was called.
     *
     * @param message the {@code Message} to send
     */
    default void reply(@NotNull MessageCreateData message) {
       getReplyContext().getBuilder().applyData(message);
        reply();
    }

    /**
     * Sends a message to the TextChannel where the button was called.
     *
     * @param builder the {@code EmbedBuilder} to send
     */
    default void reply(@NotNull EmbedBuilder builder) {
        getReplyContext().getBuilder().setEmbeds(builder.build());
        reply();
    }

    /**
     * Sends a message to the TextChannel where the button was called.
     *
     * @param embedDTO the {@link EmbedDTO} to send
     */
    default void reply(@NotNull EmbedDTO embedDTO) {
        getReplyContext().getBuilder().applyData(embedDTO.toMessageCreateData());
        reply();
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
        reply(message);
        setConsumer(success);
        reply();
    }

    /**
     * Sends a message to the TextChannel where the button was called. This method also allows to access the JDA RestAction
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
     * Sends a message to the TextChannel where the button was called. This method also allows to access the JDA RestAction
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
     * Sends a message to the TextChannel where the button was called. This method also allows to access the JDA RestAction
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

    default void setEphemeral(boolean ephemeral) {
        getReplyContext().setEphemeralReply(ephemeral);
        reply();
    }

    default void setConsumer(Consumer<Message> success) {
        getReplyContext().setConsumer(success);
        reply();
    }

    default Replyable editReply(boolean edit) {
        getReplyContext().setEditReply(edit);
        return this;
    }

    default Replyable clearComponents(boolean clear) {
        getReplyContext().setClearComponents(clear);
        return this;
    }

    /**
     * Gets the {@link ReplyContext} to use.
     *
     * @return the {@link ReplyContext}
     */
    @NotNull
    ReplyContext getReplyContext();

    void reply();

}
