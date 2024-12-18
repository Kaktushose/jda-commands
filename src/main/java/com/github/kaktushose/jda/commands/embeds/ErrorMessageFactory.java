package com.github.kaktushose.jda.commands.embeds;

import com.github.kaktushose.jda.commands.reflect.ConstraintDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Generic interface for factory classes that generate error messages.
 *
 * @see com.github.kaktushose.jda.commands.annotations.Implementation
 * @see DefaultErrorMessageFactory
 * @since 2.0.0
 */
public interface ErrorMessageFactory {

    /**
     * Gets a {@link MessageCreateData} to send when type adapting of the user input failed.
     *
     * @param event the {@link GenericInteractionCreateEvent} that was attempted to type adapt
     * @return a {@link MessageCreateData} to send when type adapting failed
     */
    MessageCreateData getTypeAdaptingFailedMessage(@NotNull GenericInteractionCreateEvent event,
                                                   @NotNull GenericInteractionDefinition definition,
                                                   @NotNull List<String> userInput);

    /**
     * Gets a {@link MessageCreateData} to send when a user is missing permissions.
     *
     * @param context the corresponding {@link Context}
     * @return a {@link MessageCreateData} to send when a user is missing permissions
     */
    MessageCreateData getInsufficientPermissionsMessage(@NotNull GenericInteractionDefinition definition);

    /**
     * Gets a {@link MessageCreateData} to send when a parameter constraint fails.
     *
     * @param context    the corresponding {@link Context}
     * @param constraint the corresponding {@link ConstraintDefinition} that failed
     * @return a {@link MessageCreateData} to send when a parameter constraint fails
     */
    MessageCreateData getConstraintFailedMessage(@NotNull ConstraintDefinition constraint);

    /**
     * Gets a {@link Message} to send when a command still has a cooldown.
     *
     * @param context the corresponding {@link Context}
     * @return a {@link MessageCreateData} to send when a command still has a cooldown
     */
    MessageCreateData getCooldownMessage(long ms);

    /**
     * Gets a {@link MessageCreateData} to send when the channel type isn't suitable for the command.
     *
     * @param context the corresponding {@link Context}
     * @return a {@link MessageCreateData} to send when the channel type isn't suitable for the command
     */
    MessageCreateData getWrongChannelTypeMessage();

    /**
     * Gets a {@link MessageCreateData} to send when the command execution failed.
     *
     * @param context   the corresponding {@link Context}
     * @param exception the Exception that made the command execution fail
     * @return a {@link MessageCreateData} to send when the command execution failed
     */
    MessageCreateData getCommandExecutionFailedMessage(@NotNull GenericInteractionCreateEvent event, @NotNull Throwable exception);

    /**
     * Gets a {@link MessageCreateData} to send when an incoming interaction already timed out.
     *
     * @param context the corresponding {@link Context}
     * @return a {@link MessageCreateData} to send when an incoming interaction already timed out
     */
    MessageCreateData getUnknownInteractionMessage();

}
