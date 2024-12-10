package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.dispatching.events.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.handling.HandlerContext;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.EphemeralInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.SequencedCollection;
import java.util.function.Function;

public record ExecutionContext<T extends GenericInteractionCreateEvent, U extends GenericInteractionDefinition>(
        T event,
        U definition,
        Runtime runtime,
        HandlerContext handlerContext,
        SequencedCollection<Object> arguments,
        Function<ExecutionContext<T, U>, GenericEvent<T, U>> eventSupplier
) {

    public void cancel(MessageCreateData errorMessage) {
         ReplyContext replyContext = new ReplyContext(this);
         replyContext.getBuilder().applyData(errorMessage);
         replyContext.queue();

        Thread.currentThread().interrupt();
    }

    public boolean ephemeral() {
        return definition instanceof EphemeralInteractionDefinition ep && ep.isEphemeral();
    }

    public ImplementationRegistry implementationRegistry() {
        return handlerContext.implementationRegistry();
    }

    public InteractionRegistry interactionRegistry() {
        return handlerContext.interactionRegistry();
    }

    public SequencedCollection<Object> arguments() {
        ArrayList<Object> actualArguments = new ArrayList<>(arguments);
        actualArguments.addFirst(eventSupplier.apply(this));
        return Collections.unmodifiableList(actualArguments);
    }
}
