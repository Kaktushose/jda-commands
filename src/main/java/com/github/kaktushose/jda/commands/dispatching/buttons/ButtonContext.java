package com.github.kaktushose.jda.commands.dispatching.buttons;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.reflect.interactions.ButtonDefinition;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * Implementation of {@link GenericContext} for {@link ButtonInteractionEvent}.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 4.0.0
 */
public class ButtonContext extends GenericContext<ButtonInteractionEvent> {

    private ButtonDefinition button;

    /**
     * Constructs a new ButtonContext.
     *
     * @param event       the corresponding {@link ButtonInteractionEvent}
     * @param jdaCommands the corresponding {@link JDACommands} instance
     */
    public ButtonContext(ButtonInteractionEvent event, JDACommands jdaCommands) {
        super(event, jdaCommands);
    }

    /**
     * Gets the {@link ButtonDefinition}.
     *
     * @return the {@link ButtonDefinition}
     */
    public ButtonDefinition getButton() {
        return button;
    }

    /**
     * Set the {@link ButtonDefinition}.
     *
     * @param button the {@link ButtonDefinition}
     * @return the current CommandContext instance
     */
    public ButtonContext setButton(ButtonDefinition button) {
        this.button = button;
        setInteraction(button);
        return this;
    }
}
