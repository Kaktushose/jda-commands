package com.github.kaktushose.jda.commands.settings;

import java.util.HashSet;
import java.util.Set;

public class GuildSettings {

    private String prefix;
    private boolean ignoreCase;
    private boolean ignoreBots;
    private boolean parseQuotes;
    private Set<String> helpLabels;
    private Set<Long> mutedChannels;
    private boolean isMutedGuild;

    public GuildSettings() {
        this("!",
                true,
                true,
                true,
                new HashSet<String>() {{
                    add("help");
                }},
                new HashSet<>(),
                false
        );
    }

    public GuildSettings(String prefix,
                         boolean ignoreCase,
                         boolean ignoreBots,
                         boolean parseQuotes,
                         Set<String> helpLabels,
                         Set<Long> mutedChannels,
                         boolean isMutedGuild) {
        this.prefix = prefix;
        this.ignoreCase = ignoreCase;
        this.ignoreBots = ignoreBots;
        this.parseQuotes = parseQuotes;
        this.helpLabels = helpLabels;
        this.mutedChannels = mutedChannels;
        this.isMutedGuild = isMutedGuild;
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

    @Override
    public String toString() {
        return "GuildSettings{" +
                "prefix='" + prefix + '\'' +
                ", ignoreCase=" + ignoreCase +
                ", ignoreBots=" + ignoreBots +
                ", parseQuotes=" + parseQuotes +
                ", helpLabels=" + helpLabels +
                ", mutedChannels=" + mutedChannels +
                ", isMuted=" + isMutedGuild +
                '}';
    }
}
