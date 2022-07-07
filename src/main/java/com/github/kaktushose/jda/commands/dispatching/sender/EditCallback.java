package com.github.kaktushose.jda.commands.dispatching.sender;

import com.github.kaktushose.jda.commands.embeds.EmbedDTO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Generic reply callback used to edit replies of {@link com.github.kaktushose.jda.commands.dispatching.GenericEvent GenericEvents}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see ReplyCallback
 * @see com.github.kaktushose.jda.commands.dispatching.sender.impl.InteractionEditCallback InteractionEditCallback
 * @since 2.3.0
 */
public interface EditCallback {

    /**
     * Edits the original message the button is attached to. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message the new {@link String} message
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    void editMessage(@NotNull String message, @Nullable Consumer<Message> success);

    /**
     * Edits the original message the button is attached to. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message the new {@link Message}
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    void editMessage(@NotNull Message message, @Nullable Consumer<Message> success);

    /**
     * Edits the original message the button is attached to. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param embed   the new {@link MessageEmbed}
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    void editMessage(@NotNull MessageEmbed embed, @Nullable Consumer<Message> success);

    /**
     * Edits the original message the button is attached to. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param builder the new {@link EmbedBuilder}
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void editMessage(@NotNull EmbedBuilder builder, @Nullable Consumer<Message> success) {
        editMessage(builder.build(), success);
    }

    /**
     * Edits the original message the button is attached to. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param builder the new {@link MessageBuilder}
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void editMessage(@NotNull MessageBuilder builder, @Nullable Consumer<Message> success) {
        editMessage(builder.build(), success);
    }

    /**
     * Edits the original message the button is attached to. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param embedDTO the new {@link EmbedDTO}
     * @param success  the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    default void editMessage(@NotNull EmbedDTO embedDTO, @Nullable Consumer<Message> success) {
        editMessage(embedDTO.toMessageEmbed(), success);
    }

    /**
     * Deletes the original message the button was attached to.
     */
    void deleteOriginal();

    /**
     * Edits the {@link LayoutComponent} attached to the message without changing the message itself.
     *
     * @param components the new {@link LayoutComponent}
     */
    void editComponents(@NotNull LayoutComponent... components);

    /**
     * No-op acknowledgement of this interaction. See
     * {@link net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback#deferEdit() IMessageEditCallback#deferEdit()}
     * for details.
     */
    void deferEdit();

}
