package com.github.kaktushose.jda.commands.dispatching.events;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.data.EmbedDTO;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ModalEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyBuilder;
import com.github.kaktushose.jda.commands.dispatching.reply.components.Buttons;
import com.github.kaktushose.jda.commands.dispatching.reply.components.Component;
import com.github.kaktushose.jda.commands.dispatching.reply.components.SelectMenus;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public sealed abstract class ReplyableEvent<T extends GenericInteractionCreateEvent> extends Event<T>
        permits ModalReplyableEvent, ComponentReplyableEvent, ModalEvent {

    public static final Logger log = LoggerFactory.getLogger(ReplyableEvent.class);
    protected final ReplyBuilder replyBuilder;

    protected ReplyableEvent(T event, InteractionRegistry interactionRegistry, Runtime runtime, boolean ephemeral) {
        super(event, interactionRegistry, runtime);
        replyBuilder = new ReplyBuilder(event, ephemeral);
    }


    public ReplyBuilder replyBuilder() {
        return replyBuilder;
    }

    /**
     * Adds an {@link ActionRow} to the reply and adds the passed {@link Component Components} to it.
     * For buttonContainers, they must be defined in the same
     * {@link com.github.kaktushose.jda.commands.annotations.interactions.Interaction Interaction} as the referring
     * {@link SlashCommand Command}.
     *
     * @param components the {@link Component Components} to add
     * @return the current instance for fluent interface
     */
    public ComponentReplyableEvent<T> with(@NotNull Component... components) {
        return new ComponentReplyableEvent<>(event, interactionRegistry, runtime, replyBuilder.ephemeral());
    }

    /**
     * Adds an {@link ActionRow} to the reply and adds the passed {@link Component Components} to it.
     * The buttonContainers must be defined in the same
     * {@link com.github.kaktushose.jda.commands.annotations.interactions.Interaction Interaction} as the referring
     * {@link SlashCommand Command}. This will enable all buttonContainers. To add
     * disabled buttonContainers, use {@link #with(Component...)}.
     *
     * @param buttons the id of the buttonContainers to add
     * @return the current instance for fluent interface
     */
    public ComponentReplyableEvent<T> withButtons(@NotNull String... buttons) {
        return with(Buttons.enabled(buttons));
    }

    /**
     * Adds an {@link ActionRow} to the reply and adds the passed {@link Component Components} to it.
     * The select menus must be defined in the same
     * {@link com.github.kaktushose.jda.commands.annotations.interactions.Interaction Interaction} as the referring
     * {@link SlashCommand Command}. This will enable all select menus. To add
     * disabled select menus, use {@link #with(Component...)}.
     *
     * @param selectMenus the id of the selectMenus to add
     * @return the current instance for fluent interface
     */
    public ReplyableEvent<T> withSelectMenus(@NotNull String... selectMenus) {
        return with(SelectMenus.enabled(selectMenus));
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed.
     *
     * @param message the message to send
     */
    public void reply(@NotNull String message) {
        replyBuilder.messageCreateBuilder().setContent(message);
        queue();
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
    public void reply(@NotNull String format, @NotNull Object... args) {
        replyBuilder.messageCreateBuilder().setContent(String.format(format, args));
        queue();
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed.
     *
     * @param message the {@code Message} to send
     */
    public void reply(@NotNull MessageCreateData message) {
        replyBuilder.messageCreateBuilder().applyData(message);
        queue();
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed.
     *
     * @param builder the {@code EmbedBuilder} to send
     */
    public void reply(@NotNull EmbedBuilder builder) {
        replyBuilder.messageCreateBuilder().setEmbeds(builder.build());
        queue();
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed.
     *
     * @param embedDTO the {@link EmbedDTO} to send
     */
    public void reply(@NotNull EmbedDTO embedDTO) {
        replyBuilder.messageCreateBuilder().setEmbeds(embedDTO.toMessageEmbed());
        queue();
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message the {@link String} message to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull String message, @Nullable Consumer<Message> success) {
        replyBuilder.messageCreateBuilder().setContent(message);
        replyBuilder.onSuccess(success);
        queue();
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param message the {@link Message} to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull MessageCreateData message, @Nullable Consumer<Message> success) {
        replyBuilder.messageCreateBuilder().applyData(message);
        replyBuilder.onSuccess(success);
        queue();
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param builder the {@link EmbedBuilder} to send
     * @param success the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull EmbedBuilder builder, @Nullable Consumer<Message> success) {
        replyBuilder.messageCreateBuilder().setEmbeds(builder.build());
        replyBuilder.onSuccess(success);
        queue();
    }

    /**
     * Sends a message to the TextChannel where the interaction was executed. This method also allows to access the JDA RestAction
     * consumer.
     *
     * @param embedDTO the {@link EmbedDTO} to send
     * @param success  the JDA RestAction success consumer
     * @see <a href="https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/RestAction.html">JDA RestAction Documentation</a>
     */
    public void reply(@NotNull EmbedDTO embedDTO, @Nullable Consumer<Message> success) {
        replyBuilder.messageCreateBuilder().setEmbeds(embedDTO.toMessageEmbed());
        replyBuilder.onSuccess(success);
        queue();
    }

    /**
     * Whether to send ephemeral replies.
     *
     * @param ephemeral {@code true} if replies should be ephemeral
     * @return the current instance for fluent interface
     */
    public ReplyableEvent<T> setEphemeral(boolean ephemeral) {
        replyBuilder.ephemeral(ephemeral);
        return this;
    }

    /**
     * Whether this reply should edit the existing message or send a new one. The default value is
     * {@code true}.
     *
     * @param edit {@code true} if this reply should edit the existing message
     * @return the current instance for fluent interface
     */
    public ReplyableEvent<T> editReply(boolean edit) {
        replyBuilder.editReply(edit);
        return this;
    }

    /**
     * Whether this reply should keep all components that are attached to the previous message. The default value is
     * {@code true}.
     *
     * @param keep {@code true} if this reply should keep all components
     * @return the current instance for fluent interface
     */
    public ReplyableEvent<T> keepComponents(boolean keep) {
        replyBuilder.keepComponents(keep);
        return this;
    }

    /**
     * Removes all components from the last message that was sent. <b>This will only work with
     * {@link #editReply(boolean)} set to true.</b>
     */
    public void removeComponents() {
        replyBuilder.removeComponents();
    }

    protected void queue() {
        replyBuilder.queue();
    }

}
