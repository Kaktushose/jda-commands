package com.github.kaktushose.jda.commands.dispatching.reply;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.function.Consumer;

public class ReplyContext {

    private final MessageCreateBuilder builder;
    Consumer<Message> consumer;
    private boolean editReply;
    private boolean clearComponents;
    private boolean ephemeralReply;

    public ReplyContext() {
        builder = new MessageCreateBuilder();
        consumer = (message) -> {};
        editReply = true;
        clearComponents = false;
        ephemeralReply = true;
    }

    public MessageCreateData toMessageCreateData() {
        return builder.build();
    }

    public MessageEditData toMessageEditData() {
        return MessageEditData.fromCreateData(toMessageCreateData());
    }

    public MessageCreateBuilder getBuilder() {
        return builder;
    }

    public Consumer<Message> getConsumer() {
        return consumer;
    }

    public ReplyContext setConsumer(Consumer<Message> consumer) {
        this.consumer = consumer;
        return this;
    }

    public boolean isEditReply() {
        return editReply;
    }

    public ReplyContext setEditReply(boolean editReply) {
        this.editReply = editReply;
        return this;
    }

    public boolean isClearComponents() {
        return clearComponents;
    }

    public ReplyContext setClearComponents(boolean clearComponents) {
        this.clearComponents = clearComponents;
        return this;
    }

    public boolean isEphemeralReply() {
        return ephemeralReply;
    }

    public ReplyContext setEphemeralReply(boolean ephemeralReply) {
        this.ephemeralReply = ephemeralReply;
        return this;
    }
}
