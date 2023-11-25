package com.github.kaktushose.jda.commands.scope;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Set;

/**
 * Interface for declaring on which Guilds a guild scoped command should be registered.
 *
 * @see com.github.kaktushose.jda.commands.annotations.Implementation
 * @see DefaultGuildScopeProvider
 * @since 4.0.0
 */
public interface GuildScopeProvider {

    /**
     * Gets a Set of guild ids the provided command should be registered for.
     *
     * @param commandData a copy of the {@link CommandData} to register
     * @return a Set of guild ids the provided command should be registered for
     */
    Set<Long> getGuildsForCommand(CommandData commandData);

}
