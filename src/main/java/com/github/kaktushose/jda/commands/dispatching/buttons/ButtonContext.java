package com.github.kaktushose.jda.commands.dispatching.buttons;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.reflect.interactions.ButtonDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ButtonContext extends GenericContext<ButtonInteractionEvent> {

    private ButtonDefinition button;

    /**
     * Constructs a new CommandContext.
     *
     * @param event       the corresponding {@link GenericInteractionCreateEvent}
     * @param jdaCommands the corresponding {@link JDACommands} instance
     */
    public ButtonContext(ButtonInteractionEvent event, JDACommands jdaCommands) {
        super(event, jdaCommands);
    }

    public ButtonDefinition getButton() {
        return button;
    }

    public ButtonContext setButton(ButtonDefinition button) {
        this.button = button;
        return this;
    }
}
