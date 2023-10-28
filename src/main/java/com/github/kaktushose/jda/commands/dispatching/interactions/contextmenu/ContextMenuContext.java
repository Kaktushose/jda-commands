package com.github.kaktushose.jda.commands.dispatching.interactions.contextmenu;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericContext;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;

public class ContextMenuContext extends GenericContext<GenericContextInteractionEvent<?>> {

    /**
     * Constructs a new ContextMenuContext.
     *
     * @param event       the corresponding {@link GenericContextInteractionEvent}
     * @param jdaCommands the corresponding {@link JDACommands} instance
     */
    public ContextMenuContext(GenericContextInteractionEvent<?> event, JDACommands jdaCommands) {
        super(event, jdaCommands);
    }
}
