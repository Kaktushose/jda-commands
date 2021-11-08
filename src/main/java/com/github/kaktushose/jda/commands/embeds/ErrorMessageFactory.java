package com.github.kaktushose.jda.commands.embeds;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import net.dv8tion.jda.api.entities.Message;

public interface ErrorMessageFactory {

    Message getCommandNotFoundMessage(CommandContext context);

    Message getInsufficientPermissionsMessage(CommandContext context);

    Message getGuildMutedMessage(CommandContext context);

    Message getChannelMutedMessage(CommandContext context);

    Message getSyntaxErrorMessage(CommandContext context);

    Message getCooldownMessage(CommandContext context, long ms);

    Message getWrongChannelTypeMessage(CommandContext context);

}
