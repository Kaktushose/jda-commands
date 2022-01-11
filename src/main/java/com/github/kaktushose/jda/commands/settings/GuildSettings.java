package com.github.kaktushose.jda.commands.settings;

import java.util.HashSet;
import java.util.Set;

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

    public GuildSettings(long guildId,
                         String prefix,
                         boolean ignoreCase,
                         boolean ignoreBots,
                         boolean parseQuotes,
                         Set<String> helpLabels,
                         Set<Long> mutedChannels,
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

    public long getGuildId() {
        return guildId;
    }

    public GuildSettings setGuildId(long guildId) {
        this.guildId = guildId;
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    public GuildSettings setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public GuildSettings setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        return this;
    }

    public boolean isIgnoreBots() {
        return ignoreBots;
    }

    public GuildSettings setIgnoreBots(boolean ignoreBots) {
        this.ignoreBots = ignoreBots;
        return this;
    }

    public boolean isParseQuotes() {
        return parseQuotes;
    }

    public void setParseQuotes(boolean parseQuotes) {
        this.parseQuotes = parseQuotes;
    }

    public Set<String> getHelpLabels() {
        return helpLabels;
    }

    public GuildSettings setHelpLabels(Set<String> helpLabels) {
        this.helpLabels = helpLabels;
        return this;
    }

    public Set<Long> getMutedChannels() {
        return mutedChannels;
    }

    public void setMutedChannels(Set<Long> mutedChannels) {
        this.mutedChannels = mutedChannels;
    }

    public boolean isMutedGuild() {
        return isMutedGuild;
    }

    public void setMutedGuild(boolean mutedGuild) {
        isMutedGuild = mutedGuild;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
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
