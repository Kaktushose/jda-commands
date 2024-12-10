package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.dispatching.ExecutionContext;
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
 * This class models a reply to an interaction. It is mainly used by the {@link Replyable} interface to construct a
 * reply but can also be accessed directly.
 *
 * @since 4.0.0
 */
public class ReplyContext {

    private static final Logger log = LoggerFactory.getLogger(ReplyContext.class);
    private final GenericInteractionCreateEvent event;
    private final MessageCreateBuilder builder;
    private Consumer<Message> success;
    private Consumer<Throwable> failure;
    private boolean editReply;
    private boolean keepComponents;
    private boolean ephemeralReply;

    /**
     * Constructs a new ReplyContext.
     *
     * @param context the corresponding {@link ExecutionContext}
     */
    public ReplyContext(ExecutionContext<?, ?> context) {
        this(context.event(), context.ephemeral());
    }

    public ReplyContext(GenericInteractionCreateEvent event, boolean ephemeral) {
        this.event = event;
        builder = new MessageCreateBuilder();
        success = (message) -> {
        };
        failure = (throwable) -> {
            log.error("The response request encountered an exception at its execution point!", new InvocationTargetException(throwable));
        };
        editReply = true;
        keepComponents = true;
        ephemeralReply = ephemeral;
    }

    public static void reply(GenericInteractionCreateEvent event, boolean ephemeral, MessageCreateData messageCreateData) {
        ReplyContext replyContext = new ReplyContext(event, ephemeral);
        replyContext.getBuilder().applyData(messageCreateData);
        replyContext.queue();
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
    public MessageCreateBuilder getBuilder() {
        return builder;
    }

    /**
     * Returns the success callback.
     *
     * @return the success callback
     */
    public Consumer<Message> getSuccessCallback() {
        return success;
    }

    /**
     * Sets the success callback.
     *
     * @return this instance
     */
    public ReplyContext setSuccessCallback(Consumer<Message> consumer) {
        this.success = consumer;
        return this;
    }

    /**
     * Returns the failure callback.
     *
     * @return the failure callback
     */
    public Consumer<Throwable> getFailureCallback() {
        return failure;
    }

    /**
     * Returns the failure callback.
     *
     * @return this instance
     */
    public ReplyContext setFailureCallback(Consumer<Throwable> consumer) {
        this.failure = consumer;
        return this;
    }

    /**
     * Whether this reply should edit the original message.
     *
     * @return {@code true} if this reply should edit the original message
     */
    public boolean isEditReply() {
        return editReply;
    }

    /**
     * Whether this reply should edit the original message.
     *
     * @param editReply {@code true} if this reply should edit the original message
     * @return this instance
     */
    public ReplyContext setEditReply(boolean editReply) {
        this.editReply = editReply;
        return this;
    }

    /**
     * Whether this reply should keep all components of the original message.
     *
     * @return {@code true} this reply should keep all components of the original message
     */
    public boolean isKeepComponents() {
        return keepComponents;
    }

    /**
     * Whether this reply should keep all components of the original message.
     *
     * @param keepComponents {@code true} this reply should keep all components of the original message
     * @return this instance
     */
    public ReplyContext setKeepComponents(boolean keepComponents) {
        this.keepComponents = keepComponents;
        return this;
    }

    /**
     * Whether this reply should be ephemeral.
     *
     * @return this instance
     */
    public boolean isEphemeralReply() {
        return ephemeralReply;
    }

    /**
     * Whether this reply should be ephemeral.
     *
     * @return {@code true} if this reply should be ephemeral
     */
    public ReplyContext setEphemeralReply(boolean ephemeralReply) {
        this.ephemeralReply = ephemeralReply;
        return this;
    }

    /**
     * Removes all components from the last message that was sent. <b>This will only work with
     * {@link #setEditReply(boolean)} set to true.</b>
     */
    public void removeComponents() {
        IReplyCallback callback;
        if (event instanceof IReplyCallback) {
            callback = (IReplyCallback) event;
        } else {
            throw new IllegalArgumentException(
                    String.format("Cannot reply to '%s'! Please report this error to the jda-commands devs!", event.getClass().getName())
            );
        }
        if (!event.isAcknowledged()) {
            callback.deferReply(false).queue();
        }
        if (editReply) {
            callback.getHook().editOriginalComponents().queue();
        } else {
            log.warn("Cannot remove components with 'editReply' set to 'false'!");
        }
    }

    /**
     * Sends the reply to Discord, taking into account all the settings that were previously made to this context.
     */
    public void queue() {
        if (editReply) {
            queueEdit();
        } else {
            queueReply();
        }
    }

    private void queueEdit() {
        IMessageEditCallback callback;
        if (event instanceof IMessageEditCallback) {
            callback = (IMessageEditCallback) event;
        } else {
            queueReply();
            return;
        }
        if (event instanceof ModalInteractionEvent) {
            if (((ModalInteractionEvent) event).getMessage() == null) {
                queueReply();
                return;
            }
        }
        if (!event.isAcknowledged()) {
            callback.deferEdit().queue();
        }
        callback.getHook().editOriginal(toMessageEditData()).queue(success, failure);
    }

    private void queueReply() {
        IReplyCallback callback;
        if (event instanceof IReplyCallback) {
            callback = (IReplyCallback) event;
        } else {
            throw new IllegalArgumentException(
                    String.format("Cannot reply to '%s'! Please report this error to the jda-commands devs!", event.getClass().getName())
            );
        }
        if (!event.isAcknowledged()) {
            callback.deferReply(ephemeralReply).queue();
        }
        callback.getHook().sendMessage(toMessageCreateData()).queue(success, failure);
    }
}
