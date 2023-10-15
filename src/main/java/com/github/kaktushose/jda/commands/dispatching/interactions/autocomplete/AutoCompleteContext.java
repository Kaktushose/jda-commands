package com.github.kaktushose.jda.commands.dispatching.interactions.autocomplete;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericContext;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public class AutoCompleteContext extends GenericContext<CommandAutoCompleteInteractionEvent> {

    /**
     * Constructs a new AutoCompleteContext.
     *
     * @param event       the corresponding {@link CommandAutoCompleteInteractionEvent}
     * @param jdaCommands the corresponding {@link JDACommands} instance
     */
    public AutoCompleteContext(CommandAutoCompleteInteractionEvent event, JDACommands jdaCommands) {
        super(event, jdaCommands);
    }
}
