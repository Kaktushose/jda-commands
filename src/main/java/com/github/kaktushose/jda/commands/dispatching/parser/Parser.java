package com.github.kaktushose.jda.commands.dispatching.parser;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import com.github.kaktushose.jda.commands.settings.SettingsProvider;
import net.dv8tion.jda.api.events.GenericEvent;

public abstract class Parser<T extends GenericEvent> {

    CommandContext parseInternal(GenericEvent event, SettingsProvider settingsProvider) {
        return parse((T) event, settingsProvider);
    }

    public abstract CommandContext parse(T event, SettingsProvider settingsProvider);

}
