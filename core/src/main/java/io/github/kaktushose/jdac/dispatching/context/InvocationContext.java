package io.github.kaktushose.jdac.dispatching.context;

import io.github.kaktushose.jdac.definitions.features.internal.Invokable;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import io.github.kaktushose.jdac.dispatching.reply.internal.ReplyAction;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory.ErrorContext;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.SequencedCollection;

/// Bundles data that is important for the execution of an interaction, especially for invoking the user defined method.
///
/// @param <T>           The used type of [GenericInteractionCreateEvent]
/// @param event         the underlying jda event
/// @param keyValueStore the [KeyValueStore] belonging to this interaction over its whole lifetime
/// @param definition    the [InteractionDefinition] defining this interaction (referring to the user defined method)
/// @param replyConfig   the [InteractionDefinition.ReplyConfig] to use
/// @param rawArguments  the arguments used to call the final user defined method via [Invokable#invoke(java.lang.Object, io.github.kaktushose.jdac.dispatching.context.InvocationContext)]
public record InvocationContext<T extends GenericInteractionCreateEvent>(
        T event,
        KeyValueStore keyValueStore,
        InteractionDefinition definition,
        InteractionDefinition.ReplyConfig replyConfig,
        SequencedCollection<@Nullable Object> rawArguments
) implements ErrorContext {

    /// @return same as [`data()#rawArguements`][InvocationContext#rawArguments()] but with [Optional]s replaced by `null`
    public SequencedCollection<@Nullable Object> arguments() {
        return rawArguments.stream()
                .map(arg -> arg instanceof Optional<?> opt ? opt.orElse(null) : arg)
                .toList();
    }

    /// Stops further execution of this invocation at the next suitable moment.
    ///
    /// @param errorMessage the error message that should be sent to the user as a reply
    /// @implNote This will interrupt the current event thread
    public void cancel(MessageCreateData errorMessage) {
        replyAction().reply(errorMessage);
        Thread.currentThread().interrupt();
    }

    /// Stops further execution of this invocation at the next suitable moment.
    ///
    /// @param component    the [MessageTopLevelComponent] that should be sent to the user as an error message
    /// @param placeholders the [Entry] placeholders to use for [message resolution][MessageResolver]
    /// @implNote This will interrupt the current event thread
    public void cancel(MessageTopLevelComponent component, Entry... placeholders) {
        replyAction().reply(component, placeholders);
        Thread.currentThread().interrupt();
    }

    private ReplyAction replyAction() {
        return new ReplyAction(new ReplyConfig(replyConfig().ephemeral(), false, false, false));
    }

    /// @return if the current invocation is canceled
    public boolean cancelled() {
        return Thread.currentThread().isInterrupted();
    }
}
