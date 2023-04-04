package com.github.kaktushose.jda.commands.settings;

import net.dv8tion.jda.api.entities.Guild;

/**
 * Interface for accessing {@link GuildSettings}.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see GuildSettings
 * @since 2.0.0
 */
public interface SettingsProvider {

    /**
     * Gets the {@link GuildSettings} for a {@link Guild}.
     *
     * @param guildId the id of the {@link Guild}
     * @return {@link GuildSettings}
     */
    GuildSettings getSettings(long guildId);

    /**
     * Gets default {@link GuildSettings} when no {@link Guild} is available
     *
     * @return {@link GuildSettings}
     */
    GuildSettings getDefaultSettings();

}
