package com.github.kaktushose.jda.commands.dispatching.router;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.reflect.interactions.SlashCommandDefinition;
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
     * Attempts to find a matching {@link SlashCommandDefinition} for the given {@link CommandContext}.
     *
     * @param context  the {@link CommandContext} to find the {@link SlashCommandDefinition} for
     * @param commands the list of {@link SlashCommandDefinition CommandDefinitions} to query
     */
    void findCommands(@NotNull CommandContext context, @NotNull Collection<SlashCommandDefinition> commands);

}
