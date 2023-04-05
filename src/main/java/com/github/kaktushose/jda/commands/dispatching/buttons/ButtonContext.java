package com.github.kaktushose.jda.commands.dispatching.buttons;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.ButtonDefinition;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ButtonContext extends GenericContext<ButtonInteractionEvent> {

    private ButtonDefinition button;

    /**
     * Constructs a new CommandContext.
     *
     * @param event       the corresponding {@link GenericInteractionCreateEvent}
     * @param jdaCommands the corresponding {@link JDACommands} instance
     * @param settings    the corresponding {@link GuildSettings}
     * @param registry    the corresponding {@link ImplementationRegistry}
     */
    public ButtonContext(ButtonInteractionEvent event,
                         JDACommands jdaCommands,
                         GuildSettings settings,
                         ImplementationRegistry registry) {
        super(event, jdaCommands, settings, registry);
    }

    public ButtonDefinition getButton() {
        return button;
    }

    public ButtonContext setButton(ButtonDefinition button) {
        this.button = button;
        return this;
    }
}