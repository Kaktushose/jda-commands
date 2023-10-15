package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.dispatching.interactions.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandContext;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
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
 * @author Kaktushose
 * @version 4.0.0
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
     * @param context the corresponding {@link CommandContext}
     */
    public ReplyContext(GenericContext<? extends GenericInteractionCreateEvent> context) {
        event = context.getEvent();
        builder = new MessageCreateBuilder();
        success = (message) -> {
        };
        failure = (throwable) -> {
            log.error("The response request encountered an exception at its execution point!", new InvocationTargetException(throwable));
        };
        editReply = true;
        keepComponents = true;
        ephemeralReply = context.isEphemeral();
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
        IReplyCallback callback;
        if (event instanceof IReplyCallback) {
            callback = (IReplyCallback) event;
        } else {
            throw new IllegalArgumentException(
                    String.format("Cannot reply to '%s'! Please report this error to the jda-commands devs!", event.getClass().getName())
            );
        }
        // The ReplyContext is also used for error messages and some appear even before acknowledging took place
        // In this case the event gets acknowledged with ephemeral set to false
        if (!event.isAcknowledged()) {
            callback.deferReply(false).queue();
        }
        callback.getHook().setEphemeral(ephemeralReply);
        if (editReply) {
            callback.getHook().editOriginal(toMessageEditData()).queue(success, failure);
            return;
        }
        callback.getHook().sendMessage(toMessageCreateData()).queue(success, failure);
    }
}
