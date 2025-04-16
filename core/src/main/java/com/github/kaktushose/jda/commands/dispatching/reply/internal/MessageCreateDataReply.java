package com.github.kaktushose.jda.commands.dispatching.reply.internal;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.MessageReply;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

public final class MessageCreateDataReply extends MessageReply {

    private MessageCreateDataReply(@NotNull GenericInteractionCreateEvent event,
                                   @NotNull InteractionDefinition definition,
                                   @NotNull InteractionDefinition.ReplyConfig replyConfig,
                                   @NotNull MessageCreateData data) {
        super(event, definition, replyConfig);
        builder.applyData(data);
    }

    public static Message reply(@NotNull GenericInteractionCreateEvent event,
                                @NotNull InteractionDefinition definition,
                                @NotNull InteractionDefinition.ReplyConfig replyConfig,
                                @NotNull MessageCreateData data) {
        return new MessageCreateDataReply(event, definition, replyConfig, data).completeInternal();
    }

    private Message completeInternal() {
        return complete();
    }

}
