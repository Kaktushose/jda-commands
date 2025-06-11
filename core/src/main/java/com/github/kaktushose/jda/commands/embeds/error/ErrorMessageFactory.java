package com.github.kaktushose.jda.commands.embeds.error;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition.ConstraintDefinition;
import com.github.kaktushose.jda.commands.extension.Implementation.ExtensionProvidable;
import io.github.kaktushose.proteus.conversion.ConversionResult;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/// Generic interface for factory classes that provide [MessageCreateData] that should be sent for common errors that
/// happen during an interaction execution, such as missing permissions or failing constraints.
///
/// @see DefaultErrorMessageFactory
/// @see JsonErrorMessageFactory
public non-sealed interface ErrorMessageFactory extends ExtensionProvidable {

    /// Gets a [MessageCreateData] to send when type adapting of the user input failed.
    ///
    /// @param context   the [ErrorContext]
    /// @param failure   the [ConversionResult.Failure]
    /// @return a [MessageCreateData] to send when type adapting failed
    
    MessageCreateData getTypeAdaptingFailedMessage(ErrorContext context, ConversionResult.Failure<?> failure);

    /// Gets a [MessageCreateData] to send when a user is missing permissions.
    ///
    /// @param context the [ErrorContext]
    /// @return a [MessageCreateData] to send when a user is missing permissions
    
    MessageCreateData getInsufficientPermissionsMessage(ErrorContext context);

    /// Gets a [MessageCreateData] to send when a parameter constraint fails.
    ///
    /// @param context    the [ErrorContext]
    /// @param constraint the corresponding [ConstraintDefinition] that failed
    /// @return a [MessageCreateData] to send when a parameter constraint fails
    
    MessageCreateData getConstraintFailedMessage(ErrorContext context, ConstraintDefinition constraint);

    /// Gets a [Message] to send when a command still has a cooldown.
    ///
    /// @param context the [ErrorContext]
    /// @param ms      the remaining cooldown in milliseconds
    /// @return a [MessageCreateData] to send when a command still has a cooldown
    
    MessageCreateData getCooldownMessage(ErrorContext context, long ms);

    /// Gets a [MessageCreateData] to send when the channel type isn't suitable for the command.
    ///
    /// @param context the [ErrorContext]
    /// @return a [MessageCreateData] to send when the channel type isn't suitable for the command
    
    MessageCreateData getWrongChannelTypeMessage(ErrorContext context);

    /// Gets a [MessageCreateData] to send when the command execution failed.
    ///
    /// @param context   the [ErrorContext]
    /// @param exception the [Throwable] that made the command execution fail
    /// @return a [MessageCreateData] to send when the command execution failed
    
    MessageCreateData getCommandExecutionFailedMessage(ErrorContext context, Throwable exception);

    /// Gets a [MessageCreateData] to send when an incoming component interaction already timed out.
    ///
    /// @param event the [GenericInteractionCreateEvent] mo runtime was found for
    /// @return a [MessageCreateData] to send when an incoming component interaction already timed out
    
    MessageCreateData getTimedOutComponentMessage(GenericInteractionCreateEvent event);

    /// Holds the respective [GenericInteractionCreateEvent] and [InteractionDefinition] of an error.
    interface ErrorContext {

        /// The [GenericInteractionCreateEvent] in whose execution the error occurred.
        
        GenericInteractionCreateEvent event();

        /// The [InteractionDefinition] that models the interaction of the [#event].
        
        InteractionDefinition definition();

    }

}
