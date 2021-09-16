package com.github.kaktushose.jda.commands.settings;

import net.dv8tion.jda.api.entities.Guild;

public interface SettingsProvider {

    GuildSettings getSettings(Guild guild);

}
