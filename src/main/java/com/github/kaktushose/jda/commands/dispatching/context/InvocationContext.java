package com.github.kaktushose.jda.commands.dispatching.context;

import com.github.kaktushose.jda.commands.definitions.features.internal.Invokable;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.MessageReply;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory.ErrorContext;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.SequencedCollection;

/// Bundles data that is important for the execution of an interaction, especially for invoking the user defined method.
///
/// @param <T>           The used type of [GenericInteractionCreateEvent]
/// @param event         the underlying jda event
/// @param keyValueStore the [KeyValueStore] belonging to this interaction over its whole lifetime
/// @param arguments     the arguments used to call the final user defined method via [Invokable#invoke(java.lang.Object, com.github.kaktushose.jda.commands.dispatching.context.InvocationContext)]
/// @param definition    the [InteractionDefinition] defining this interaction (referring to the user defined method)
public record InvocationContext<T extends GenericInteractionCreateEvent>(
        @NotNull T event,
        @NotNull KeyValueStore keyValueStore,
        @NotNull InteractionDefinition definition,
        @NotNull SequencedCollection<Object> arguments
) implements ErrorContext {
    /// Stops further execution of this invocation at the next suitable moment.
    ///
    /// @param errorMessage the error message that should be sent to the user as a reply
    /// @implNote This will interrupt the current event thread
    public void cancel(@NotNull MessageCreateData errorMessage) {
        new MessageReply(event, definition, new InteractionDefinition.ReplyConfig()).reply(errorMessage);

        Thread.currentThread().interrupt();
    }
}
