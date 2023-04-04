package com.github.kaktushose.jda.commands.dispatching.reply;

import com.github.kaktushose.jda.commands.dispatching.commands.CommandContext;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.function.Consumer;

public class ReplyContext {

    private final SlashCommandInteractionEvent event;
    private final MessageCreateBuilder builder;
    private Consumer<Message> consumer;
    private boolean editReply;
    private boolean clearComponents;
    private boolean ephemeralReply;

    public ReplyContext(CommandContext context) {
        event = context.getEvent();
        builder = new MessageCreateBuilder();
        consumer = (message) -> {};
        editReply = true;
        clearComponents = false;
        ephemeralReply = context.isEphemeral();
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

    public void queue() {
        // The ReplyContext is also used for error messages and some appear even before acknowledging took place
        // In this case the event gets acknowledged with ephemeral set to false
        if (!event.isAcknowledged()) {
            event.deferReply(false).queue();
        }
        event.getHook().setEphemeral(ephemeralReply);
        if (editReply) {
            event.getHook().editOriginal(toMessageEditData()).queue(consumer);
            return;
        }
        event.getHook().sendMessage(toMessageCreateData()).queue(consumer);
    }
}
