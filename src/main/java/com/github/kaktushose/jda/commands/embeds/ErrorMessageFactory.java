package com.github.kaktushose.jda.commands.embeds;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.SlashCommandContext;
import com.github.kaktushose.jda.commands.reflect.ConstraintDefinition;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

/**
 * Generic interface for factory classes that generate error messages.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see DefaultErrorMessageFactory
 * @since 2.0.0
 */
public interface ErrorMessageFactory {

    /**
     * Gets a {@link MessageCreateData} to send when type adapting of the user input failed.
     *
     * @param context the corresponding {@link SlashCommandContext}
     * @return a {@link MessageCreateData} to send when type adapting failed
     */
    MessageCreateData getTypeAdaptingFailedMessage(@NotNull SlashCommandContext context);

    /**
     * Gets a {@link MessageCreateData} to send when a user is missing permissions.
     *
     * @param context the corresponding {@link SlashCommandContext}
     * @return a {@link MessageCreateData} to send when a user is missing permissions
     */
    MessageCreateData getInsufficientPermissionsMessage(@NotNull SlashCommandContext context);

    /**
     * Gets a {@link MessageCreateData} to send when a {@link net.dv8tion.jda.api.entities.Guild Guild} is muted.
     *
     * @param context the corresponding {@link Context}
     * @return a {@link MessageCreateData} to send when a {@link net.dv8tion.jda.api.entities.Guild Guild} is muted
     */
    MessageCreateData getGuildMutedMessage(@NotNull Context context);

    /**
     * Gets a {@link MessageCreateData} to send when a {@link net.dv8tion.jda.api.entities.channel.concrete.TextChannel TextChannel} is muted.
     *
     * @param context the corresponding {@link Context}
     * @return a {@link MessageCreateData} to send when a {@link net.dv8tion.jda.api.entities.channel.concrete.TextChannel TextChannel} is muted
     */
    MessageCreateData getChannelMutedMessage(@NotNull Context context);

    /**
     * Gets a {@link MessageCreateData} to send when a parameter constraint fails.
     *
     * @param context    the corresponding {@link Context}
     * @param constraint the corresponding {@link ConstraintDefinition} that failed
     * @return a {@link MessageCreateData} to send when a parameter constraint fails
     */
    MessageCreateData getConstraintFailedMessage(@NotNull Context context, @NotNull ConstraintDefinition constraint);

    /**
     * Gets a {@link Message} to send when a command still has a cooldown.
     *
     * @param context the corresponding {@link Context}
     * @return a {@link MessageCreateData} to send when a command still has a cooldown
     */
    MessageCreateData getCooldownMessage(@NotNull Context context, long ms);

    /**
     * Gets a {@link MessageCreateData} to send when the channel type isn't suitable for the command.
     *
     * @param context the corresponding {@link Context}
     * @return a {@link MessageCreateData} to send when the channel type isn't suitable for the command
     */
    MessageCreateData getWrongChannelTypeMessage(@NotNull Context context);

    /**
     * Gets a {@link MessageCreateData} to send when the command execution failed.
     *
     * @param context   the corresponding {@link Context}
     * @param exception the Exception that made the command execution fail
     * @return a {@link MessageCreateData} to send when the command execution failed
     */
    MessageCreateData getCommandExecutionFailedMessage(@NotNull Context context, @NotNull Throwable exception);

    /**
     * Gets a {@link MessageCreateData} to send when an incoming interaction already timed out.
     *
     * @param context the corresponding {@link Context}
     * @return a {@link MessageCreateData} to send when an incoming interaction already timed out
     */
    MessageCreateData getUnknownInteractionMessage(@NotNull Context context);

}
