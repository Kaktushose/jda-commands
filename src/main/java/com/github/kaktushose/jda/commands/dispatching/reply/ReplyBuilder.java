package com.github.kaktushose.jda.commands.dispatching.reply;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

/**
 * This class models a reply to an interaction. It is mainly used by the
 * {@link com.github.kaktushose.jda.commands.dispatching.events.ReplyableEvent ReplyableEvent} class to construct a
 * reply but can also be accessed directly.
 *
 * @since 4.0.0
 */
public final class ReplyBuilder {

    private static final Logger log = LoggerFactory.getLogger(ReplyBuilder.class);
    private final GenericInteractionCreateEvent event;
    private final MessageCreateBuilder builder;
    private Consumer<Message> success;
    private Consumer<Throwable> failure;
    private boolean editReply;
    private boolean keepComponents;
    private boolean ephemeral;

    public ReplyBuilder(GenericInteractionCreateEvent event, boolean ephemeral) {
        this.event = event;
        builder = new MessageCreateBuilder();
        success = (_) -> {
        };
        failure = (throwable) -> {
            log.error("The response request encountered an exception at its execution point!", new InvocationTargetException(throwable));
        };
        editReply = true;
        keepComponents = true;
        this.ephemeral = ephemeral;
    }

    public static void reply(GenericInteractionCreateEvent event, boolean ephemeral, MessageCreateData messageCreateData) {
        var builder = new ReplyBuilder(event, ephemeral);
        builder.messageCreateBuilder().applyData(messageCreateData);
        builder.queue();
    }

    /**
     * Transforms this ReplyContext to a {@link MessageCreateData}. This is equal to {@code getBuilder().build()}.
     *
     * @return the {@link MessageCreateData}
     */
    public MessageCreateData toMessageCreateData() {
        return builder.build();
    }

    /**
     * Transforms this ReplyContext to a {@link MessageEditData}.
     *
     * @return the {@link MessageEditData}
     */
    public MessageEditData toMessageEditData() {
        return MessageEditData.fromCreateData(toMessageCreateData());
    }

    /**
     * Returns the underlying {@link MessageCreateBuilder}.
     *
     * @return the underlying {@link MessageCreateBuilder}
     */
    public MessageCreateBuilder messageCreateBuilder() {
        return builder;
    }

    /**
     * Sets the success callback.
     *
     * @return this instance
     */
    public ReplyBuilder onSuccess(Consumer<Message> consumer) {
        this.success = consumer;
        return this;
    }

    /**
     * Returns the failure callback.
     *
     * @return this instance
     */
    public ReplyBuilder onFailure(Consumer<Throwable> consumer) {
        this.failure = consumer;
        return this;
    }

    /**
     * Whether this reply should edit the original message.
     *
     * @return {@code true} if this reply should edit the original message
     */
    public boolean editReply() {
        return editReply;
    }

    /**
     * Whether this reply should edit the original message.
     *
     * @param editReply {@code true} if this reply should edit the original message
     * @return this instance
     */
    public ReplyBuilder editReply(boolean editReply) {
        this.editReply = editReply;
        return this;
    }

    /**
     * Whether this reply should keep all components of the original message.
     *
     * @return {@code true} this reply should keep all components of the original message
     */
    public boolean keepComponents() {
        return keepComponents;
    }

    /**
     * Whether this reply should keep all components of the original message.
     *
     * @param keepComponents {@code true} this reply should keep all components of the original message
     * @return this instance
     */
    public ReplyBuilder keepComponents(boolean keepComponents) {
        this.keepComponents = keepComponents;
        return this;
    }

    /**
     * Whether this reply should be ephemeral.
     *
     * @return {@code true} of this reply should be ephemeral
     */
    public boolean ephemeral() {
        return ephemeral;
    }

    /**
     * Whether this reply should be ephemeral.
     *
     * @return {@code true} if this reply should be ephemeral
     */
    public ReplyBuilder ephemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
        return this;
    }

    /**
     * Removes all components from the last message that was sent.
     */
    public void removeComponents() {
        if (event instanceof IReplyCallback callback) {
            if (!event.isAcknowledged()) {
                callback.deferReply(false).queue();
            }
            callback.getHook().editOriginalComponents().queue();
        }
    }

    /**
     * Sends the reply to Discord, taking into account all the settings that were previously made to this context.
     */
    public void queue() {
        switch (event) {
            case ModalInteractionEvent modalEvent when modalEvent.getMessage() != null && editReply ->
                    queueEdit((IMessageEditCallback) event);
            case IMessageEditCallback callback when editReply -> queueEdit(callback);
            default -> queueReply();
        }
    }

    private void queueEdit(IMessageEditCallback callback) {
        if (!event.isAcknowledged()) {
            callback.deferEdit().queue();
        }
        callback.getHook().editOriginal(toMessageEditData()).queue(success, failure);
    }

    private void queueReply() {
        if (event instanceof IReplyCallback callback) {
            if (!event.isAcknowledged()) {
                callback.deferReply(ephemeral).queue();
            }
            callback.getHook().sendMessage(toMessageCreateData()).queue(success, failure);
        } else {
            throw new IllegalArgumentException(
                    String.format("Cannot reply to '%s'! Please report this error to the jda-commands devs!", event.getClass().getName())
            );
        }
    }
}
