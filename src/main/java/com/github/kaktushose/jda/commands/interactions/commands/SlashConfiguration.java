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
    private final boolean helpEnabled;
    private final CommandRegistrationPolicy policy;

    /**
     * Constructs a new SlashConfiguration.
     *
     * @param guildIds {@link List} of guild ids to register commands for
     * @param global {@code true} if global scope should be used
     * @param helpEnabled {@code true} if help commands should be auto generated
     * @param policy the {@link CommandRegistrationPolicy} to use
     */
    public SlashConfiguration(@NotNull List<Long> guildIds, boolean global, boolean helpEnabled, @NotNull CommandRegistrationPolicy policy) {
        this.guildIds = guildIds;
        this.global = global;
        this.helpEnabled = helpEnabled;
        this.policy = policy;
    }

    /**
     * Gets a {@link List} of guild ids to register commands for.
     *
     * @return a {@link List} of guild ids to register commands for
     */
    public List<Long> getGuildIds() {
        return Collections.unmodifiableList(guildIds);
    }

    /**
     * Whether to register commands globally.
     *
     * @return {@code true} if global scope should be used
     */
    public boolean isGlobal() {
        return global;
    }

    /**
     * Gets the {@link CommandRegistrationPolicy} to use.
     *
     * @return the {@link CommandRegistrationPolicy}
     */
    public CommandRegistrationPolicy getPolicy() {
        return policy;
    }

    /**
     * Whether to register help commands.
     *
     * @return {@code true} if help commands should be auto generated
     */
    public boolean isHelpEnabled() {
        return helpEnabled;
    }
}
