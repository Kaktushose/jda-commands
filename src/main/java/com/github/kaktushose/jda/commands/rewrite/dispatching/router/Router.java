package com.github.kaktushose.jda.commands.rewrite.dispatching.router;

import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.rewrite.reflect.CommandDefinition;

import java.util.Collection;

public interface Router {

    void findCommands(CommandContext context, Collection<CommandDefinition> commands);

}
