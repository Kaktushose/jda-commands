package com.github.kaktushose.jda.commands.dispatching.buttons;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public class ButtonContext extends GenericContext {

    /**
     * Constructs a new CommandContext.
     *
     * @param jdaCommands the corresponding {@link JDACommands} instance
     * @param settings    the corresponding {@link GuildSettings}
     * @param registry    the corresponding {@link ImplementationRegistry}
     * @param event       the corresponding {@link GenericInteractionCreateEvent}
     */
    public ButtonContext(JDACommands jdaCommands,
                         GuildSettings settings,
                         ImplementationRegistry registry,
                         GenericInteractionCreateEvent event) {
        super(event, jdaCommands, settings, registry);
    }
}
