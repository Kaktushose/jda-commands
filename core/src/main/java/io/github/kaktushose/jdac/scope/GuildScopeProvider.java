package io.github.kaktushose.jdac.scope;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Set;
import java.util.function.Function;

/// Interface for declaring on which Guilds a guild scoped command should be registered.
///
/// @see DefaultGuildScopeProvider
@FunctionalInterface
public interface GuildScopeProvider extends Function<CommandData, Set<Long>> {

    /// Gets a Set of guild ids the provided command should be registered for.
    ///
    /// @param commandData a copy of the [CommandData] to register
    /// @return a Set of guild ids the provided command should be registered for
    Set<Long> apply(CommandData commandData);

}
