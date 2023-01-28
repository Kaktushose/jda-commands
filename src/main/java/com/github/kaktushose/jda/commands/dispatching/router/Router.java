package com.github.kaktushose.jda.commands.dispatching.router;

import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import org.jetbrains.annotations.NotNull;

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
     * Attempts to find a matching {@link CommandDefinition} for the given {@link GenericContext}.
     *
     * @param context  the {@link GenericContext} to find the {@link CommandDefinition} for
     * @param commands the list of {@link CommandDefinition CommandDefinitions} to query
     */
    void findCommands(@NotNull GenericContext context, @NotNull Collection<CommandDefinition> commands);

}
