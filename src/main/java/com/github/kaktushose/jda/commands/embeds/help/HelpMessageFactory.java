package com.github.kaktushose.jda.commands.embeds.help;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.reflect.ControllerDefinition;
import net.dv8tion.jda.api.entities.Message;

import java.util.Set;

public interface HelpMessageFactory {

    Message getSpecificHelp(CommandContext context);

    Message getGenericHelp(Set<ControllerDefinition> controllers, CommandContext context);

}
