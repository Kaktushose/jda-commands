package com.github.kaktushose.jda.commands.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Default implementation of {@link SettingsProvider}. This implementation works on a global level and doesn't support
 * guild specific settings. Settings can also be defined inside a <em>jdac.properties</em> file inside the resources
 * folder.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see SettingsProvider
 * @since 2.0.0
 */
public class DefaultSettingsProvider implements SettingsProvider {

    private static final Logger log = LoggerFactory.getLogger(DefaultSettingsProvider.class);
    private final GuildSettings settings;

    /**
     * Constructs a new DefaultSettingsProvider. If present, attempts to load values from the <em>jdac.properties</em>
     * file, else will use the same default values as {@link GuildSettings#GuildSettings}
     */
    public DefaultSettingsProvider() {
        settings = new GuildSettings();
        try {
            Properties properties = new Properties();
            properties.load(DefaultSettingsProvider.class.getClassLoader().getResourceAsStream("jdac.properties"));

            settings.setHelpLabel(properties.getProperty("helpLabel", "help"));
            settings.setEphemeralHelp(Boolean.parseBoolean(properties.getProperty("ephemeralHelp", "true")));

            String mutedChannels = properties.getProperty("mutedChannels", "");
            String[] channels = mutedChannels.split(", ");
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
            log.debug("jdac.properties missing or malformed, using default values");
        }
    }

    @Override
    public GuildSettings getSettings(long id) {
        return settings;
    }

    @Override
    public GuildSettings getDefaultSettings() {
        return settings;
    }
}
