package com.github.kaktushose.jda.commands.embeds.error;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition.ConstraintDefinition;
import com.github.kaktushose.jda.commands.extension.Implementation.ExtensionProvidable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/// Generic interface for factory classes that provide [MessageCreateData] that should be sent for common errors that
/// happen during an interaction execution, such as missing permissions or failing constraints.
///
/// @see DefaultErrorMessageFactory
public non-sealed interface ErrorMessageFactory extends ExtensionProvidable {

    /// Gets a [MessageCreateData] to send when type adapting of the user input failed.
    ///
    /// @param context   the [ErrorContext]
    /// @param userInput the input the user provided
    /// @return a [MessageCreateData] to send when type adapting failed
    @NotNull
    MessageCreateData getTypeAdaptingFailedMessage(@NotNull ErrorContext context, @NotNull List<String> userInput);

    /// Gets a [MessageCreateData] to send when a user is missing permissions.
    ///
    /// @param context the [ErrorContext]
    /// @return a [MessageCreateData] to send when a user is missing permissions
    @NotNull
    MessageCreateData getInsufficientPermissionsMessage(@NotNull ErrorContext context);

    /// Gets a [MessageCreateData] to send when a parameter constraint fails.
    ///
    /// @param context    the [ErrorContext]
    /// @param constraint the corresponding [ConstraintDefinition] that failed
    /// @return a [MessageCreateData] to send when a parameter constraint fails
    @NotNull
    MessageCreateData getConstraintFailedMessage(@NotNull ErrorContext context, @NotNull ConstraintDefinition constraint);

    /// Gets a [Message] to send when a command still has a cooldown.
    ///
    /// @param context the [ErrorContext]
    /// @param ms      the remaining cooldown in milliseconds
    /// @return a [MessageCreateData] to send when a command still has a cooldown
    @NotNull
    MessageCreateData getCooldownMessage(@NotNull ErrorContext context, long ms);

    /// Gets a [MessageCreateData] to send when the command execution failed.
    ///
    /// @param context   the [ErrorContext]
    /// @param exception the [Throwable] that made the command execution fail
    /// @return a [MessageCreateData] to send when the command execution failed
    @NotNull
    MessageCreateData getCommandExecutionFailedMessage(@NotNull ErrorContext context, @NotNull Throwable exception);

    /// Gets a [MessageCreateData] to send when an incoming component interaction already timed out.
    ///
    /// @param event the [GenericInteractionCreateEvent] mo runtime was found for
    /// @return a [MessageCreateData] to send when an incoming component interaction already timed out
    @NotNull
    MessageCreateData getTimedOutComponentMessage(@NotNull GenericInteractionCreateEvent event);

    /// Holds the respective [GenericInteractionCreateEvent] and [InteractionDefinition] of an error.
    interface ErrorContext {

        /// The [GenericInteractionCreateEvent] in whose execution the error occurred.
        @NotNull
        GenericInteractionCreateEvent event();

        /// The [InteractionDefinition] that models the interaction of the [#event].
        @NotNull
        InteractionDefinition definition();

    }

}
