package io.github.kaktushose.jdac.scope;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Collections;
import java.util.Set;

/// Default implementation of [GuildScopeProvider]. **This will always return an empty Set!**
///
/// @see GuildScopeProvider
public class DefaultGuildScopeProvider implements GuildScopeProvider {

    /// @return always an empty set
    @Override
    public Set<Long> apply(CommandData commandData) {
        return Collections.emptySet();
    }
}
