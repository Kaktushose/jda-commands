package com.github.kaktushose.jda.commands.dispatching.context;

import com.github.kaktushose.jda.commands.definitions.features.internal.Invokable;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.internal.ReplyAction;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory.ErrorContext;
import com.github.kaktushose.jda.commands.message.MessageResolver;
import com.github.kaktushose.jda.commands.message.i18n.I18n;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.SequencedCollection;

/// Bundles data that is important for the execution of an interaction, especially for invoking the user defined method.
///
/// @param <T>           The used type of [GenericInteractionCreateEvent]
/// @param data          The data hold by this [InvocationContext], including the event, interaction definition etc.
/// @param util          A set of framework functionalities exposed to the user, including [I18n] etc.
public record InvocationContext<T extends GenericInteractionCreateEvent>(
        Utility util,
        Data<T> data
) implements ErrorContext {

    @Override
    public GenericInteractionCreateEvent event() {
        return data.event;
    }

    @Override
    public InteractionDefinition definition() {
        return data.definition;
    }

    /// An object providing access to some framework functionality.
    ///
    /// @param i18n             The [I18n] class used by JDA-Commands
    /// @param messageResolver  The [MessageResolver] class used by JDA-Commands
    public record Utility(
            I18n i18n,
            MessageResolver messageResolver
    ) {}

    /// An object holding data exposed to the user that is crucial for invoking the user defined method.
    ///
    /// @param <T>           The used type of [GenericInteractionCreateEvent]
    /// @param event         the underlying jda event
    /// @param keyValueStore the [KeyValueStore] belonging to this interaction over its whole lifetime
    /// @param definition    the [InteractionDefinition] defining this interaction (referring to the user defined method)
    /// @param replyConfig   the [InteractionDefinition.ReplyConfig] to use
    /// @param rawArguments  the arguments used to call the final user defined method via [Invokable#invoke(java.lang.Object, com.github.kaktushose.jda.commands.dispatching.context.InvocationContext)]
    public record Data<T extends GenericInteractionCreateEvent>(
            T event,
            KeyValueStore keyValueStore,
            InteractionDefinition definition,
            InteractionDefinition.ReplyConfig replyConfig,
            SequencedCollection<@Nullable Object> rawArguments
    ) {}

    /// @return same as [#rawArguments()] but with [Optional]s replaced by `null`
    public SequencedCollection<@Nullable Object> arguments() {
        return data.rawArguments.stream()
                .map(arg -> arg instanceof Optional<?> opt ? opt.orElse(null) : arg)
                .toList();
    }

    /// Stops further execution of this invocation at the next suitable moment.
    ///
    /// @param errorMessage the error message that should be sent to the user as a reply
    /// @implNote This will interrupt the current event thread
    public void cancel(MessageCreateData errorMessage) {
        var errorReplyConfig = new InteractionDefinition.ReplyConfig(data.replyConfig().ephemeral(), false, false, data.replyConfig.editReply());
        new ReplyAction(errorReplyConfig).reply(errorMessage);
        Thread.currentThread().interrupt();
    }

    /// @return if the current invocation is cancelled
    public boolean cancelled() {
        return Thread.currentThread().isInterrupted();
    }
}
