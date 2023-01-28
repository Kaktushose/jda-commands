package com.github.kaktushose.jda.commands.embeds.error;

import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.reflect.ConstraintDefinition;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
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
     * Gets a {@link Message} to send when a user is missing permissions.
     *
     * @param context the corresponding {@link GenericContext}
     * @return a {@link Message} to send when a user is missing permissions
     */
    MessageCreateData getInsufficientPermissionsMessage(@NotNull GenericContext context);

    /**
     * Gets a {@link Message} to send when a {@link net.dv8tion.jda.api.entities.Guild Guild} is muted.
     *
     * @param context the corresponding {@link GenericContext}
     * @return a {@link Message} to send when a {@link net.dv8tion.jda.api.entities.Guild Guild} is muted
     */
    MessageCreateData getGuildMutedMessage(@NotNull GenericContext context);

    /**
     * Gets a {@link Message} to send when a {@link net.dv8tion.jda.api.entities.TextChannel TextChannel} is muted.
     *
     * @param context the corresponding {@link GenericContext}
     * @return a {@link Message} to send when a {@link net.dv8tion.jda.api.entities.TextChannel TextChannel} is muted
     */
    MessageCreateData getChannelMutedMessage(@NotNull GenericContext context);

    /**
     * Gets a {@link Message} to send when a {@link net.dv8tion.jda.api.entities.User User} is muted.
     *
     * @param context the corresponding {@link GenericContext}
     * @return a {@link Message} to send when a {@link net.dv8tion.jda.api.entities.User User} is muted
     */
    MessageCreateData getUserMutedMessage(@NotNull GenericContext context);

    /**
     * Gets a {@link Message} to send when a parameter constraint fails.
     *
     * @param context    the corresponding {@link GenericContext}
     * @param constraint the corresponding {@link ConstraintDefinition} that failed
     * @return a {@link Message} to send when a parameter constraint fails
     */
    MessageCreateData getConstraintFailedMessage(@NotNull GenericContext context, @NotNull ConstraintDefinition constraint);

    /**
     * Gets a {@link Message} to send when a command still has a cooldown.
     *
     * @param context the corresponding {@link GenericContext}
     * @return a {@link Message} to send when a command still has a cooldown
     */
    MessageCreateData getCooldownMessage(@NotNull GenericContext context, long ms);

    /**
     * Gets a {@link Message} to send when the channel type isn't suitable for the command.
     *
     * @param context the corresponding {@link GenericContext}
     * @return a {@link Message} to send when the channel type isn't suitable for the command
     */
    MessageCreateData getWrongChannelTypeMessage(@NotNull GenericContext context);

    /**
     * Gets a {@link Message} to send when the command execution failed.
     *
     * @param context   the corresponding {@link GenericContext}
     * @param exception the Exception that made the command execution fail
     * @return a {@link Message} to send when the command execution failed
     */
    MessageCreateData getCommandExecutionFailedMessage(@NotNull GenericContext context, @NotNull Exception exception);

}
