package com.github.kaktushose.jda.commands.settings;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Container for all command execution settings.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see GuildSettings
 * @since 2.0.0
 */
public class GuildSettings {

    private long guildId;
    private String prefix;
    private boolean ignoreCase;
    private boolean ignoreBots;
    private boolean parseQuotes;
    private Set<String> helpLabels;
    private Set<Long> mutedChannels;
    private boolean isMutedGuild;
    private int maxDistance;

    /**
     * Constructs a new GuildSettings object with the following default values.
     * <ul>
     *     <li>prefix: !</li>
     *     <li>ignoreCase: true</li>
     *     <li>ignoreBots: true</li>
     *     <li>parseQuotes: true</li>
     *     <li>helpLabels: help</li>
     *     <li>mutedChannels: none</li>
     *     <li>isMutedGuild: false</li>
     *     <li>maxDistance: 3</li>
     * </ul>
     */
    public GuildSettings() {
        this(0,
                "!",
                true,
                true,
                true,
                new HashSet<String>() {{
                    add("help");
                }},
                new HashSet<>(),
                false,
                3
        );
    }

    /**
     * Constructs a new GuildSettings object.
     *
     * @param guildId       the guild id
     * @param prefix        the prefix to use
     * @param ignoreCase    whether to ignore the case
     * @param ignoreBots    whether to ignore the case
     * @param parseQuotes   whether to concatenate quotes, e.g. {@code foo "quote string" bar} ->
     *                      {@code ["foo", "quote string", "bar"]}
     * @param helpLabels    a set of all help labels
     * @param mutedChannels a set of muted channels
     * @param isMutedGuild  whether this guild should be ignored completely
     * @param maxDistance   the maximal Levenshtein distance to use when routing commands
     */
    public GuildSettings(long guildId,
                         @NotNull String prefix,
                         boolean ignoreCase,
                         boolean ignoreBots,
                         boolean parseQuotes,
                         @NotNull Set<String> helpLabels,
                         @NotNull Set<Long> mutedChannels,
                         boolean isMutedGuild,
                         int maxDistance) {
        this.guildId = guildId;
        this.prefix = prefix;
        this.ignoreCase = ignoreCase;
        this.ignoreBots = ignoreBots;
        this.parseQuotes = parseQuotes;
        this.helpLabels = helpLabels;
        this.mutedChannels = mutedChannels;
        this.isMutedGuild = isMutedGuild;
        this.maxDistance = maxDistance;
    }

    /**
     * Gets the guild id.
     *
     * @return the guild id
     */
    public long getGuildId() {
        return guildId;
    }

    public GuildSettings setGuildId(long guildId) {
        this.guildId = guildId;
        return this;
    }

    /**
     * Gets the prefix.
     *
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix.
     *
     * @param prefix the new prefix
     * @return this instance
     */
    public GuildSettings setPrefix(@NotNull String prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * Whether to ignore the case.
     *
     * @return {@code true} if the case should be ignored
     */
    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    /**
     * Whether to ignore the case.
     *
     * @param ignoreCase {@code true} if the case should be ignored
     * @return this instance
     */
    public GuildSettings setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        return this;
    }

    /**
     * Whether to ignore bot accounts.
     *
     * @return {@code true} if bot accounts should be ignored
     */
    public boolean isIgnoreBots() {
        return ignoreBots;
    }

    /**
     * Whether to  bot accounts.
     *
     * @param ignoreBots {@code true} if  bot accounts should be ignored
     * @return this instance
     */
    public GuildSettings setIgnoreBots(boolean ignoreBots) {
        this.ignoreBots = ignoreBots;
        return this;
    }

    /**
     * Whether to parse quotes.
     *
     * @return {@code true} if quotes should be parsed
     */
    public boolean isParseQuotes() {
        return parseQuotes;
    }

    /**
     * Whether to parse quotes.
     *
     * @param parseQuotes {@code true} if quotes should be parsed
     * @return this instance
     */
    public GuildSettings setParseQuotes(boolean parseQuotes) {
        this.parseQuotes = parseQuotes;
        return this;
    }

    /**
     * Gets a set of all help labels.
     *
     * @return a set of all help labels
     */
    public Set<String> getHelpLabels() {
        return helpLabels;
    }

    /**
     * Sets the help labels.
     *
     * @param helpLabels a set of help labels
     * @return this instance
     */
    public GuildSettings setHelpLabels(@NotNull Collection<String> helpLabels) {
        this.helpLabels = new HashSet<>(helpLabels);
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

    /**
     * Gets the maximal Levenshtein distance to use when routing commands.
     *
     * @return the maximal Levenshtein distance to use when routing commands
     */
    public int getMaxDistance() {
        return maxDistance;
    }

    /**
     * Sets the maximal Levenshtein distance to use when routing commands.
     *
     * @param maxDistance the maximal Levenshtein distance to use when routing commands
     * @return this instance
     */
    public GuildSettings setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
        return this;
    }

    @Override
    public String toString() {
        return "GuildSettings{" +
                "id=" + guildId +
                ", prefix='" + prefix + '\'' +
                ", ignoreCase=" + ignoreCase +
                ", ignoreBots=" + ignoreBots +
                ", parseQuotes=" + parseQuotes +
                ", helpLabels=" + helpLabels +
                ", mutedChannels=" + mutedChannels +
                ", isMuted=" + isMutedGuild +
                ", maxDistance=" + maxDistance +
                '}';
    }
}
