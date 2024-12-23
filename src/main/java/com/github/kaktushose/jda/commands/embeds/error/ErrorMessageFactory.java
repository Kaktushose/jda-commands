package com.github.kaktushose.jda.commands.embeds.error;

import com.github.kaktushose.jda.commands.annotations.Implementation;
import com.github.kaktushose.jda.commands.definitions.reflect.misc.ConstraintDefinition;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/// Generic interface for factory classes that generate error messages.
///
/// @see Implementation
/// @see DefaultErrorMessageFactory
/// @since 2.0.0
public interface ErrorMessageFactory {

    /// Gets a [MessageCreateData] to send when type adapting of the user input failed.
    ///
    /// @param event the [GenericInteractionCreateEvent] that was attempted to type adapt
    /// @return a [MessageCreateData] to send when type adapting failed
    @NotNull
    MessageCreateData getTypeAdaptingFailedMessage(@NotNull GenericInteractionCreateEvent event,
                                                   @NotNull GenericInteractionDefinition definition,
                                                   @NotNull List<String> userInput);

    /// Gets a [MessageCreateData] to send when a user is missing permissions.
    ///
    /// @param definition the corresponding [GenericInteractionDefinition]
    /// @return a [MessageCreateData] to send when a user is missing permissions
    @NotNull
    MessageCreateData getInsufficientPermissionsMessage(@NotNull GenericInteractionDefinition definition);

    /// Gets a [MessageCreateData] to send when a parameter constraint fails.
    ///
    /// @param constraint the corresponding [ConstraintDefinition] that failed
    /// @return a [MessageCreateData] to send when a parameter constraint fails
    @NotNull
    MessageCreateData getConstraintFailedMessage(@NotNull ConstraintDefinition constraint);

    /// Gets a [Message] to send when a command still has a cooldown.
    ///
    /// @param ms the remaining cooldown in milliseconds
    /// @return a [MessageCreateData] to send when a command still has a cooldown
    @NotNull
    MessageCreateData getCooldownMessage(long ms);

    /// Gets a [MessageCreateData] to send when the channel type isn't suitable for the command.
    ///
    /// @return a [MessageCreateData] to send when the channel type isn't suitable for the command
    @NotNull
    MessageCreateData getWrongChannelTypeMessage();

    /// Gets a [MessageCreateData] to send when the command execution failed.
    ///
    /// @param event     the corresponding [GenericInteractionCreateEvent]
    /// @param exception the [Throwable] that made the command execution fail
    /// @return a [MessageCreateData] to send when the command execution failed
    @NotNull
    MessageCreateData getCommandExecutionFailedMessage(@NotNull GenericInteractionCreateEvent event, @NotNull Throwable exception);

    /// Gets a [MessageCreateData] to send when an incoming component interaction already timed out.
    ///
    /// @return a [MessageCreateData] to send when an incoming component interaction already timed out
    @NotNull
    MessageCreateData getTimedOutComponentMessage();

}
