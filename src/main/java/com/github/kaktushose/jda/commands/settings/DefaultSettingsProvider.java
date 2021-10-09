package com.github.kaktushose.jda.commands.settings;

import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class DefaultSettingsProvider implements SettingsProvider {

    private static final Logger log = LoggerFactory.getLogger(DefaultSettingsProvider.class);
    private final GuildSettings settings;

    public DefaultSettingsProvider() {
        settings = new GuildSettings();
        try {
            Properties properties = new Properties();
            properties.load(DefaultSettingsProvider.class.getClassLoader().getResourceAsStream("jdac.properties"));

            settings.setPrefix(properties.getProperty("prefix", "!"));
            settings.setIgnoreCase(Boolean.parseBoolean(properties.getProperty("ignoreCase", "true")));
            settings.setIgnoreBots(Boolean.parseBoolean(properties.getProperty("ignoreBots", "true")));
            settings.setParseQuotes(Boolean.parseBoolean(properties.getProperty("parseQuotes", "true")));

            String helpLabels = properties.getProperty("helpLabels", "help");
            String[] labels = helpLabels.split(" ,");
            settings.getHelpLabels().clear();
            for (String label : labels) {
                settings.getHelpLabels().add(label.trim());
            }

            String mutedChannels = properties.getProperty("mutedChannels", "");
            String[] channels = mutedChannels.split(" ,");
            settings.getMutedChannels().clear();
            for (String channel : channels) {
                if (channel.isEmpty()) {
                    continue;
                }
                try {
                    settings.getMutedChannels().add(Long.parseLong(channel.trim()));
                } catch (NumberFormatException e) {
                    log.error(String.format("Cannot parse %s to long", channel), e);
                }
            }

            log.debug("Loaded settings from jdac.properties file");
        } catch (IOException | NullPointerException ignored) {
            log.debug("No jdac.properties file found, using default values");
        }
    }

    @Override
    public GuildSettings getSettings(@Nullable Guild guild) {
        return settings;
    }
}
