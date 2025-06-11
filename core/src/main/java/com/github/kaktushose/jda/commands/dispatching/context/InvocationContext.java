package com.github.kaktushose.jda.commands.dispatching.context;

import com.github.kaktushose.jda.commands.definitions.features.internal.Invokable;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.internal.MessageCreateDataReply;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory.ErrorContext;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jspecify.annotations.Nullable;

import java.util.SequencedCollection;

/// Bundles data that is important for the execution of an interaction, especially for invoking the user defined method.
///
/// @param <T>           The used type of [GenericInteractionCreateEvent]
/// @param event         the underlying jda event
/// @param keyValueStore the [KeyValueStore] belonging to this interaction over its whole lifetime
/// @param definition    the [InteractionDefinition] defining this interaction (referring to the user defined method)
/// @param replyConfig   the [InteractionDefinition.ReplyConfig] to use
/// @param arguments     the arguments used to call the final user defined method via [Invokable#invoke(java.lang.Object, com.github.kaktushose.jda.commands.dispatching.context.InvocationContext)]
public record InvocationContext<T extends GenericInteractionCreateEvent>(
        T event,
        KeyValueStore keyValueStore,
        InteractionDefinition definition,
        InteractionDefinition.ReplyConfig replyConfig,
        SequencedCollection<@Nullable Object> arguments
) implements ErrorContext {
    /// Stops further execution of this invocation at the next suitable moment.
    ///
    /// @param errorMessage the error message that should be sent to the user as a reply
    /// @implNote This will interrupt the current event thread
    public void cancel(MessageCreateData errorMessage) {
        var errorReplyConfig = new InteractionDefinition.ReplyConfig(replyConfig().ephemeral(), false, false, replyConfig.editReply());
        MessageCreateDataReply.reply(event, definition, errorReplyConfig, errorMessage);

        Thread.currentThread().interrupt();
    }
}
