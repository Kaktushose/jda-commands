package com.github.kaktushose.jda.commands.slash;

import java.util.List;

/**
 * Container class holding the configuration for registering slash commands. This class is only used internally and can
 * be ignored.
 *
 *   @author Kaktushose
 *   @version 2.3.0
 *   @see com.github.kaktushose.jda.commands.JDACommandsSlashBuilder JDACommandsSlashBuilder
 *   @since 2.3.0
 */
public class SlashConfiguration {

    private final List<Long> guildIds;
    private final boolean global;
    private final CommandRegistrationPolicy policy;

    public SlashConfiguration(List<Long> guildIds, boolean global, CommandRegistrationPolicy policy) {
        this.guildIds = guildIds;
        this.global = global;
        this.policy = policy;
    }

    List<Long> getGuildIds() {
        return guildIds;
    }

    boolean isGlobal() {
        return global;
    }

    CommandRegistrationPolicy getPolicy() {
        return policy;
    }
}
