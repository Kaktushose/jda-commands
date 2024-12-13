package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.dispatching.handling.HandlerContext;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyBuilder;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.EphemeralInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.SequencedCollection;
import java.util.function.Function;

public record InvocationContext<T extends GenericInteractionCreateEvent>(
        T event,
        KeyValueStore keyValueStore,
        GenericInteractionDefinition definition,
        SequencedCollection<Object> arguments,
        Function<GenericInteractionDefinition, Object> instanceSupplier,

        // todo move out of here
        HandlerContext handlerContext

) {
    public void cancel(MessageCreateData errorMessage) {
        ReplyBuilder replyBuilder = new ReplyBuilder(event, ephemeral());
        replyBuilder.messageCreateBuilder().applyData(errorMessage);
        replyBuilder.queue();

        Thread.currentThread().interrupt();
    }

    // todo: move out of here
    public boolean ephemeral() {
        return ephemeral(definition);
    }

    public static boolean ephemeral(GenericInteractionDefinition definition) {
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
