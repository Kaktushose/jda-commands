package com.github.kaktushose.jda.commands.dispatching.context;

import com.github.kaktushose.jda.commands.dispatching.reply.MessageReply;
import com.github.kaktushose.jda.commands.reflect.interactions.EphemeralInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.ReplyConfig;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.SequencedCollection;

public record InvocationContext<T extends GenericInteractionCreateEvent>(
        T event,
        KeyValueStore keyValueStore,
        GenericInteractionDefinition definition,
        SequencedCollection<Object> arguments
) {
    public void cancel(MessageCreateData errorMessage) {
        new MessageReply(event, replyConfig(definition)).reply(errorMessage);

        Thread.currentThread().interrupt();
    }

    private ReplyConfig replyConfig(GenericInteractionDefinition definition) {
        return definition instanceof EphemeralInteractionDefinition ephemeral
                ? ephemeral.replyConfig()
                : new ReplyConfig();
    }
}
