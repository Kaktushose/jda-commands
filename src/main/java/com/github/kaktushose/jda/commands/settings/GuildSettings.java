package com.github.kaktushose.jda.commands.settings;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Container for all command execution settings.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see GuildSettings
 * @since 2.0.0
 */
public class GuildSettings {

    private long guildId;
    private Set<Long> mutedChannels;
    private boolean isMutedGuild;

    /**
     * Constructs a new GuildSettings object with the following default values.
     * <ul>
     *     <li>mutedChannels: none</li>
     *     <li>isMutedGuild: false</li>
     * </ul>
     */
    public GuildSettings() {
        this(0,  new HashSet<>(), false);
    }

    /**
     * Constructs a new GuildSettings object.
     *
     * @param guildId       the guild id
     * @param mutedChannels a set of muted channels
     * @param isMutedGuild  whether this guild should be ignored completely
     */
    public GuildSettings(long guildId, @NotNull Set<Long> mutedChannels, boolean isMutedGuild) {
        this.guildId = guildId;
        this.mutedChannels = mutedChannels;
        this.isMutedGuild = isMutedGuild;
    }

    /**
     * Gets the guild id.
     *
     * @return the guild id
     */
    public long getGuildId() {
        return guildId;
    }

    /**
     * Sets the guild id.
     *
     * @param guildId the guild id
     * @return this instance
     */
    public GuildSettings setGuildId(long guildId) {
        this.guildId = guildId;
        return this;
    }

    /**
     * Gets a set of muted channel ids.
     *
     * @return a set of muted channel ids
     */
    public Set<Long> getMutedChannels() {
        return mutedChannels;
    }

    /**
     * Sets the muted channels.
     *
     * @param mutedChannels a set of muted channel ids
     * @return this instance
     */
    public GuildSettings setMutedChannels(@NotNull Collection<Long> mutedChannels) {
        this.mutedChannels = new HashSet<>(mutedChannels);
        return this;
    }

    /**
     * Whether this guild should be ignored.
     *
     * @return {@code true} if this guild should be ignored
     */
    public boolean isMutedGuild() {
        return isMutedGuild;
    }

    /**
     * Whether this guild should be ignored.
     *
     * @param mutedGuild {@code true} if this guild should be ignored
     * @return this instance
     */
    public GuildSettings setMutedGuild(boolean mutedGuild) {
        isMutedGuild = mutedGuild;
        return this;
    }

    @Override
    public String toString() {
        return "GuildSettings{" +
                "guildId=" + guildId +
                ", mutedChannels=" + mutedChannels +
                ", isMutedGuild=" + isMutedGuild +
                '}';
    }
}
