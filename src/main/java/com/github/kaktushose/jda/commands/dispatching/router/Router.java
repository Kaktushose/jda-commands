package com.github.kaktushose.jda.commands.dispatching.router;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;

import java.util.Collection;

public interface Router {

    void findCommands(CommandContext context, Collection<CommandDefinition> commands);

    boolean parseHelpMessage(CommandContext context);

}
