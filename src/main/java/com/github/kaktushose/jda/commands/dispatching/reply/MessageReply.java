package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.reflect.interactions.ReplyConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IDeferrableCallback;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public sealed class MessageReply implements Reply permits ConfigurableReply {

    protected static final Logger log = LoggerFactory.getLogger(MessageReply.class);
    protected final GenericInteractionCreateEvent event;
    protected final MessageCreateBuilder builder;
    protected boolean ephemeral;
    protected boolean editReply;
    protected boolean keepComponents;

    public MessageReply(GenericInteractionCreateEvent event, ReplyConfig replyConfig) {
        this.event = event;
        this.ephemeral = replyConfig.ephemeral();
        this.editReply = replyConfig.editReply();
        this.keepComponents = replyConfig.keepComponents();
        this.builder = new MessageCreateBuilder();
    }

    public MessageReply(MessageReply reply) {
        this.event = reply.event;
        this.builder = reply.builder;
        this.ephemeral = reply.ephemeral;
        this.editReply = reply.editReply;
        this.keepComponents = reply.keepComponents;
    }

    public Message reply(@NotNull String message) {
        builder.setContent(message);
        return complete();
    }

    public Message reply(@NotNull MessageCreateData message) {
        builder.applyData(message);
        return complete();
    }

    public Message reply(@NotNull EmbedBuilder builder) {
        this.builder.setEmbeds(builder.build());
        return complete();
    }

    protected Message complete() {
        switch (event) {
            case ModalInteractionEvent modalEvent when modalEvent.getMessage() != null && editReply -> deferEdit(modalEvent);
            case IMessageEditCallback callback when editReply -> deferEdit(callback);
            case IReplyCallback callback -> deferReply(callback);
            default -> throw new IllegalArgumentException(
                    "Cannot reply to '%s'! Please report this error to the devs of jda-commands!".formatted(event.getClass().getName())
            );
        }
        var hook = ((IDeferrableCallback) event).getHook();
        if (editReply) {
            if (keepComponents) {
                builder.addComponents(hook.retrieveOriginal().complete().getComponents());
            }
            return hook.editOriginal(MessageEditData.fromCreateData(builder.build())).complete();
        }
        return hook.setEphemeral(ephemeral).sendMessage(builder.build()).complete();
    }

    protected void deferReply(IReplyCallback callback) {
        if (!event.isAcknowledged()) {
            callback.deferReply(ephemeral).queue();
        }
    }

    protected void deferEdit(IMessageEditCallback callback) {
        if (!event.isAcknowledged()) {
            callback.deferEdit().queue();
        }
    }
}
