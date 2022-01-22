package com.github.kaktushose.jda.commands.dispatching.router;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;

import java.util.Collection;

/**
 * Generic top level interface for defining command routers.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public interface Router {

    /**
     * Attempts to find a matching {@link CommandDefinition} for the given {@link CommandContext}.
     *
     * @param context  the {@link CommandContext} to find the {@link CommandDefinition} for
     * @param commands the list of {@link CommandDefinition CommandDefinitions} to query
     */
    void findCommands(CommandContext context, Collection<CommandDefinition> commands);

}
