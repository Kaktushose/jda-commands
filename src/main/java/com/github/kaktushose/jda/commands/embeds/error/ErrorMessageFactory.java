package com.github.kaktushose.jda.commands.embeds.error;

import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.reflect.ConstraintDefinition;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/**
 * Generic interface for factory classes that generate error messages.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see DefaultErrorMessageFactory
 * @since 2.0.0
 */
public interface ErrorMessageFactory {

    /**
     * Gets a {@link Message} to send when no command was found.
     *
     * @param context the corresponding {@link GenericContext}
     * @return a {@link Message} to send when no command was found
     */
    Message getCommandNotFoundMessage(@NotNull GenericContext context);

    /**
     * Gets a {@link Message} to send when a user is missing permissions.
     *
     * @param context the corresponding {@link GenericContext}
     * @return a {@link Message} to send when a user is missing permissions
     */
    Message getInsufficientPermissionsMessage(@NotNull GenericContext context);

    /**
     * Gets a {@link Message} to send when a {@link net.dv8tion.jda.api.entities.Guild Guild} is muted.
     *
     * @param context the corresponding {@link GenericContext}
     * @return a {@link Message} to send when a {@link net.dv8tion.jda.api.entities.Guild Guild} is muted
     */
    Message getGuildMutedMessage(@NotNull GenericContext context);

    /**
     * Gets a {@link Message} to send when a {@link net.dv8tion.jda.api.entities.TextChannel TextChannel} is muted.
     *
     * @param context the corresponding {@link GenericContext}
     * @return a {@link Message} to send when a {@link net.dv8tion.jda.api.entities.TextChannel TextChannel} is muted
     */
    Message getChannelMutedMessage(@NotNull GenericContext context);

    /**
     * Gets a {@link Message} to send when a {@link net.dv8tion.jda.api.entities.User User} is muted.
     *
     * @param context the corresponding {@link GenericContext}
     * @return a {@link Message} to send when a {@link net.dv8tion.jda.api.entities.User User} is muted
     */
    Message getUserMutedMessage(@NotNull GenericContext context);

    /**
     * Gets a {@link Message} to send when the user input has a syntax error.
     *
     * @param context the corresponding {@link GenericContext}
     * @return a {@link Message} to send when the user input has a syntax error
     */
    Message getSyntaxErrorMessage(@NotNull GenericContext context);

    /**
     * Gets a {@link Message} to send when a parameter constraint fails.
     *
     * @param context    the corresponding {@link GenericContext}
     * @param constraint the corresponding {@link ConstraintDefinition} that failed
     * @return a {@link Message} to send when a parameter constraint fails
     */
    Message getConstraintFailedMessage(@NotNull GenericContext context, @NotNull ConstraintDefinition constraint);

    /**
     * Gets a {@link Message} to send when a command still has a cooldown.
     *
     * @param context the corresponding {@link GenericContext}
     * @return a {@link Message} to send when a command still has a cooldown
     */
    Message getCooldownMessage(@NotNull GenericContext context, long ms);

    /**
     * Gets a {@link Message} to send when the channel type isn't suitable for the command.
     *
     * @param context the corresponding {@link GenericContext}
     * @return a {@link Message} to send when the channel type isn't suitable for the command
     */
    Message getWrongChannelTypeMessage(@NotNull GenericContext context);

    /**
     * Gets a {@link Message} to send when the command execution failed.
     *
     * @param context   the corresponding {@link GenericContext}
     * @param exception the Exception that made the command execution fail
     * @return a {@link Message} to send when the command execution failed
     */
    Message getCommandExecutionFailedMessage(@NotNull GenericContext context, @NotNull Exception exception);

}
