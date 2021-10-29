package com.github.kaktushose.jda.commands.embeds;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import com.github.kaktushose.jda.commands.reflect.ControllerDefinition;
import net.dv8tion.jda.api.entities.Message;

import java.util.Set;

public interface HelpMessageFactory {

    Message getSpecificHelp(CommandDefinition command, CommandContext context);

    Message getGenericHelp(Set<ControllerDefinition> controllers, CommandContext context);

}
