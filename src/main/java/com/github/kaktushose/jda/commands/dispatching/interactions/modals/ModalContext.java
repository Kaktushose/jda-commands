package com.github.kaktushose.jda.commands.dispatching.interactions.modals;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericContext;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

public class ModalContext extends GenericContext<ModalInteractionEvent> {

    /**
     * Constructs a new ModalContext.
     *
     * @param event       the corresponding {@link ModalContext}
     * @param jdaCommands the corresponding {@link JDACommands} instance
     */
    public ModalContext(ModalInteractionEvent event, JDACommands jdaCommands) {
        super(event, jdaCommands);
    }
}
