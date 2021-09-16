package com.github.kaktushose.jda.commands.settings.impl;

import com.github.kaktushose.jda.commands.settings.GuildSettings;
import com.github.kaktushose.jda.commands.settings.SettingsProvider;
import net.dv8tion.jda.api.entities.Guild;
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

            log.debug("Loaded settings from jdac.properties file");
        } catch (IOException | NullPointerException ignored) {
            log.debug("No jdac.properties file found, using default values");
        }
    }

    @Override
    public GuildSettings getSettings(Guild guild) {
        return settings;
    }
}
