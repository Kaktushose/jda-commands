package com.github.kaktushose.jda.commands.dispatching.reply;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public sealed class MessageReply implements Reply permits ConfigurableReply {

    protected static final Logger log = LoggerFactory.getLogger(MessageReply.class);
    protected final GenericInteractionCreateEvent event;
    protected final MessageCreateBuilder builder;
    protected boolean ephemeral;
    protected boolean editReply;
    protected boolean keepComponents;
    protected Consumer<Message> success;
    protected Consumer<? super Throwable> failure;

    public MessageReply(GenericInteractionCreateEvent event,
                        boolean ephemeral) {
        this.event = event;
        this.ephemeral = ephemeral;
        this.builder = new MessageCreateBuilder();
        success = (_) -> {
        };
        failure = RestAction.getDefaultFailure();
        editReply = true;
        keepComponents = true;
    }

    public MessageReply(MessageReply reply) {
        this.event = reply.event;
        this.builder = reply.builder;
        this.ephemeral = reply.ephemeral;
        this.editReply = true;
        this.keepComponents = true;
        this.success = reply.success;
        this.failure = reply.failure;
    }

    public void reply(@NotNull String message) {
        builder.setContent(message);
        queue();
    }

    public void reply(@NotNull MessageCreateData message) {
        builder.applyData(message);
        queue();
    }

    public void reply(@NotNull EmbedBuilder builder) {
        this.builder.setEmbeds(builder.build());
        queue();
    }

    protected void queue() {
        switch (event) {
            case ModalInteractionEvent modalEvent when modalEvent.getMessage() != null && editReply ->
                    queueEdit((IMessageEditCallback) event);
            case IMessageEditCallback callback when editReply -> queueEdit(callback);
            default -> queueReply();
        }
    }

    protected void queueReply() {
        if (event instanceof IReplyCallback callback) {
            if (!event.isAcknowledged()) {
                callback.deferReply(ephemeral).queue();
            }
            callback.getHook().sendMessage(builder.build()).queue(success, failure);
        } else {
            throw new IllegalArgumentException(
                    String.format("Cannot reply to '%s'! Please report this error to the devs of jda-commands!", event.getClass().getName())
            );
        }
    }

    protected void queueEdit(IMessageEditCallback callback) {
        if (!event.isAcknowledged()) {
            callback.deferEdit().queue();
        }
        callback.getHook().editOriginal(MessageEditData.fromCreateData(builder.build())).queue(success, failure);
    }
}
