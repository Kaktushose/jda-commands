package com.github.kaktushose.jda.commands.interactions.commands;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Container class holding the configuration for registering slash commands. This class is only used internally and can
 * be ignored.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see com.github.kaktushose.jda.commands.JDACommandsSlashBuilder JDACommandsSlashBuilder
 * @since 2.3.0
 */
public class SlashConfiguration {

    private final List<Long> guildIds;
    private final boolean global;
    private final CommandRegistrationPolicy policy;

    public SlashConfiguration(@NotNull List<Long> guildIds, boolean global, @NotNull CommandRegistrationPolicy policy) {
        this.guildIds = guildIds;
        this.global = global;
        this.policy = policy;
    }

    public List<Long> getGuildIds() {
        return Collections.unmodifiableList(guildIds);
    }

    public boolean isGlobal() {
        return global;
    }

    public CommandRegistrationPolicy getPolicy() {
        return policy;
    }
}
