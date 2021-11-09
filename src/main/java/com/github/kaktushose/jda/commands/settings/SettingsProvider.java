package com.github.kaktushose.jda.commands.settings;

import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;

public interface SettingsProvider {

    GuildSettings getSettings(@Nullable Guild guild);

}
