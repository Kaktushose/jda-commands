package com.github.kaktushose.jda.commands.dispatching.context;

import com.github.kaktushose.jda.commands.definitions.ReplyConfig;
import com.github.kaktushose.jda.commands.definitions.features.Invokeable;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.MessageReply;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.SequencedCollection;

/// Bundles data that is important for the execution of an interaction, especially for invoking the user defined method.
///
/// @param <T>           The used type of [GenericInteractionCreateEvent]
/// @param event         the underlying jda event
/// @param keyValueStore the [KeyValueStore] belonging to this interaction over its whole lifetime
/// @param arguments     the arguments used to call the final user defined method via [Invokeable#invoke(java.lang.Object, com.github.kaktushose.jda.commands.dispatching.context.InvocationContext)]
/// @param definition    the [InteractionDefinition] defining this interaction (referring to the user defined method)
public record InvocationContext<T extends GenericInteractionCreateEvent>(
        T event,
        KeyValueStore keyValueStore,
        InteractionDefinition definition,
        SequencedCollection<Object> arguments
) {
    /// Stops further execution of this invocation at the next suitable moment.
    ///
    /// @implNote This will interrupt the current event thread
    /// @param errorMessage the error message that should be sent to the user as a reply
    public void cancel(MessageCreateData errorMessage) {
        new MessageReply(event, definition, new ReplyConfig()).reply(errorMessage);

        Thread.currentThread().interrupt();
    }
}
