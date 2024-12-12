package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.dispatching.handling.HandlerContext;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyBuilder;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.EphemeralInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public record InvocationContext<T extends GenericInteractionCreateEvent>(
        T event,
        KeyValueStore keyValueStore,

        // todo: move out of here
        GenericInteractionDefinition definition,
        HandlerContext handlerContext,

        // todo: idk what to do about that
        String runtimeId

) {
    public void cancel(MessageCreateData errorMessage) {
        ReplyBuilder replyBuilder = new ReplyBuilder(event, ephemeral());
        replyBuilder.messageCreateBuilder().applyData(errorMessage);
        replyBuilder.queue();

        Thread.currentThread().interrupt();
    }

    // todo: move out of here
    public boolean ephemeral() {
        return definition instanceof EphemeralInteractionDefinition ep && ep.isEphemeral();
    }

    // todo: move out of here
    public ImplementationRegistry implementationRegistry() {
        return handlerContext.implementationRegistry();
    }

    // todo: move out of here
    public InteractionRegistry interactionRegistry() {
        return handlerContext.interactionRegistry();
    }
}
