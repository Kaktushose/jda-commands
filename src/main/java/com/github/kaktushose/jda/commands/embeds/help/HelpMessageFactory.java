package com.github.kaktushose.jda.commands.embeds.help;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import com.github.kaktushose.jda.commands.reflect.ControllerDefinition;
import net.dv8tion.jda.api.entities.Message;

import java.util.Set;

public interface HelpMessageFactory {

    //TODO do we really need the command separate? Since its also in the context
    Message getSpecificHelp(CommandDefinition command, CommandContext context);

    Message getGenericHelp(Set<ControllerDefinition> controllers, CommandContext context);

}
