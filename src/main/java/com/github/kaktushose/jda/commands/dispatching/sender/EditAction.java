package com.github.kaktushose.jda.commands.dispatching.sender;

import com.github.kaktushose.jda.commands.embeds.EmbedDTO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Generic interface holding edit methods. Uses a {@link EditCallback} to edit the replies.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see ReplyAction
 * @see EditCallback
 * @since 2.3.0
 */
public interface EditAction {

    /**
     * Edits the original message the button is attached to.
     *
     * @param message the new message
     */
    default void edit(@NotNull String message) {
        edit(message, (Consumer<Message>) null);
    }

    /**
     * Edits the original message the button is attached to using the specified format string and arguments.
     *
     * @param format the new message
     * @param args   Arguments referenced by the format specifiers in the format string. If there are more arguments than
     *               format specifiers, the extra arguments are ignored. The number of arguments is variable and may be
     *               zero.
     * @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format specifier that
     *                                          is incompatible with the given arguments, insufficient arguments given
     *                                          the format string, or other illegal conditions.
     */
    default void edit(@NotNull String format, @NotNull Object... args) {
        edit(String.format(format, args));
    }

    /**
     * Edits the original message the button is attached to.
     *
     * @param message the new {@code Message}
     */
    default void edit(@NotNull Message message) {
        edit(message, null);
    }

    /**
     * Edits the original message the button is attached to.
     *
     * @param builder the new {@code MessageBuilder}
     */
    default void edit(@NotNull MessageBuilder builder) {
        edit(builder, null);
    }


    /**
     * Edits the original message the button is attached to.
     *
     * @param builder the new {@code EmbedBuilder}
     */
    default void edit(@NotNull EmbedBuilder builder) {
        edit(builder, null);
    }

    /**
     * Edits the original message the button is attached to.
     *
     * @param embedDTO the new {@link EmbedDTO}
     */
    default void edit(@NotNull EmbedDTO embedDTO) {
        edit(embedDTO, null);
    }

    /**
     * Edits the original message the button is attached to. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message the new message
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void edit(@NotNull String message, @Nullable Consumer<Message> success) {
        getEditCallback().editMessage(message, success);
    }

    /**
     * Edits the original message the button is attached to. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message the new {@link Message}
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void edit(@NotNull Message message, @Nullable Consumer<Message> success) {
        getEditCallback().editMessage(message, success);
    }

    /**
     * Edits the original message the button is attached to. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param builder the new {@link EmbedBuilder}
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void edit(@NotNull EmbedBuilder builder, @Nullable Consumer<Message> success) {
        getEditCallback().editMessage(builder, success);
    }

    /**
     * Edits the original message the button is attached to. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param builder the new {@link MessageBuilder}
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void edit(@NotNull MessageBuilder builder, @Nullable Consumer<Message> success) {
        getEditCallback().editMessage(builder, success);
    }

    /**
     * Edits the original message the button is attached to. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param embedDTO the new {@link EmbedDTO}
     * @param success  the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void edit(@NotNull EmbedDTO embedDTO, @Nullable Consumer<Message> success) {
        getEditCallback().editMessage(embedDTO, success);
    }

    /**
     * Edits the buttons attached to the message without changing the message itself. The buttons must be defined in the
     * same {@link com.github.kaktushose.jda.commands.annotations.CommandController CommandController} as the referring
     * {@link com.github.kaktushose.jda.commands.annotations.Command Command}.
     *
     * @param buttons the ids of the new buttons
     * @return the current instance for fluent interface
     */
    EditAction editButtons(String... buttons);

    /**
     * Deletes the original message the button was attached to.
     */
    default EditAction deleteOriginal() {
        getEditCallback().deleteOriginal();
        return this;
    }

    /**
     * Removes all components form the original message.
     *
     * @return the current instance for fluent interface
     */
    default EditAction clearComponents() {
        getEditCallback().editComponents();
        return this;
    }

    /**
     * Gets the {@link EditCallback} to use.
     *
     * @return the {@link EditCallback}
     */
    @NotNull
    EditCallback getEditCallback();

}
