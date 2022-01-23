package com.github.kaktushose.jda.commands.settings;

import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for accessing {@link GuildSettings}.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see GuildSettings
 * @since 2.0.0
 */
public interface SettingsProvider {

    /**
     * Gets the {@link GuildSettings} for a {@link Guild}.
     *
     * @param guild the {@link Guild} to get the {@link GuildSettings} for
     * @return {@link GuildSettings}
     */
    GuildSettings getSettings(@Nullable Guild guild);

}
