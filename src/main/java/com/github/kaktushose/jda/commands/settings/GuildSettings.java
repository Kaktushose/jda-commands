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
    private String helpLabel;
    private Set<Long> mutedChannels;
    private boolean isMutedGuild;
    private boolean ephemeralHelp;

    /**
     * Constructs a new GuildSettings object with the following default values.
     * <ul>
     *     <li>helpLabels: help</li>
     *     <li>mutedChannels: none</li>
     *     <li>isMutedGuild: false</li>
     *     <li>ephemeralHelp: true</li>
     * </ul>
     */
    public GuildSettings() {
        this(0, "help", new HashSet<>(), false, true);
    }

    /**
     * Constructs a new GuildSettings object.
     *
     * @param guildId       the guild id
     * @param helpLabel     the help label
     * @param mutedChannels a set of muted channels
     * @param isMutedGuild  whether this guild should be ignored completely
     * @param ephemeralHelp whether help replies should be ephemeral
     */
    public GuildSettings(long guildId,
                         @NotNull String helpLabel,
                         @NotNull Set<Long> mutedChannels,
                         boolean isMutedGuild,
                         boolean ephemeralHelp) {
        this.guildId = guildId;
        this.helpLabel = helpLabel;
        this.mutedChannels = mutedChannels;
        this.isMutedGuild = isMutedGuild;
        this.ephemeralHelp = ephemeralHelp;
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
     * Gets a set of all help labels.
     *
     * @return a set of all help labels
     */
    @NotNull
    public String getHelpLabel() {
        return helpLabel;
    }

    /**
     * Sets the help labels.
     *
     * @param helpLabel the help label
     * @return this instance
     */
    public GuildSettings setHelpLabel(@NotNull String helpLabel) {
        this.helpLabel = helpLabel;
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

    public boolean isEphemeralHelp() {
        return ephemeralHelp;
    }

    public void setEphemeralHelp(boolean ephemeralHelp) {
        this.ephemeralHelp = ephemeralHelp;
    }

    @Override
    public String toString() {
        return "GuildSettings{" +
                "guildId=" + guildId +
                ", helpLabel='" + helpLabel + '\'' +
                ", mutedChannels=" + mutedChannels +
                ", isMutedGuild=" + isMutedGuild +
                ", ephemeralHelp=" + ephemeralHelp +
                '}';
    }
}
