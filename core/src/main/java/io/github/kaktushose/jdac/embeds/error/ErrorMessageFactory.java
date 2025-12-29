package io.github.kaktushose.jdac.embeds.error;

import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.proteus.conversion.ConversionResult;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

/// Generic interface for factory classes that provide a [MessageTopLevelComponent] that should be sent for common errors
/// that happen during an interaction execution, such as missing permissions or failing constraints.
///
/// @see DefaultErrorMessageFactory
public interface ErrorMessageFactory {

    /// Gets a [MessageTopLevelComponent] to send when type adapting of the user input failed.
    ///
    /// @param context the [ErrorContext]
    /// @param failure the [ConversionResult.Failure]
    /// @return a [MessageTopLevelComponent] to send when type adapting failed
    MessageTopLevelComponent getTypeAdaptingFailedMessage(ErrorContext context, ConversionResult.Failure<?> failure);

    /// Gets a [MessageTopLevelComponent] to send when a user is missing permissions.
    ///
    /// @param context the [ErrorContext]
    /// @return a [MessageTopLevelComponent] to send when a user is missing permissions
    MessageTopLevelComponent getInsufficientPermissionsMessage(ErrorContext context);

    /// Gets a [MessageTopLevelComponent] to send when a parameter constraint fails.
    ///
    /// @param context the [ErrorContext]
    /// @return a [MessageTopLevelComponent] to send when a parameter constraint fails
    MessageTopLevelComponent getConstraintFailedMessage(ErrorContext context, String message);

    /// Gets a [MessageTopLevelComponent] to send when the command execution failed.
    ///
    /// @param context   the [ErrorContext]
    /// @param exception the [Throwable] that made the command execution fail
    /// @return a [MessageTopLevelComponent] to send when the command execution failed
    MessageTopLevelComponent getInteractionExecutionFailedMessage(ErrorContext context, Throwable exception);

    /// Gets a [MessageTopLevelComponent] to send when an incoming component interaction already timed out.
    ///
    /// @param event the [GenericInteractionCreateEvent] mo runtime was found for
    /// @return a [MessageTopLevelComponent] to send when an incoming component interaction already timed out
    MessageTopLevelComponent getTimedOutComponentMessage(GenericInteractionCreateEvent event);

    /// Holds the respective [GenericInteractionCreateEvent] and [InteractionDefinition] of an error.
    interface ErrorContext {

        /// The [GenericInteractionCreateEvent] in whose execution the error occurred.
        GenericInteractionCreateEvent event();

        /// The [InteractionDefinition] that models the interaction of the [#event].
        InteractionDefinition definition();

    }

}
