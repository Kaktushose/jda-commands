package com.github.kaktushose.jda.commands.dispatching.commands.autocomplete;

import com.github.kaktushose.jda.commands.dispatching.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.Collection;

public class AutoCompleteEvent extends GenericEvent {

    private final CommandAutoCompleteInteractionEvent event;

    protected AutoCompleteEvent(AutoCompleteContext context) {
        super(GenericEvent.fromEvent(context.getEvent()));
        event = context.getEvent();
    }

    public void replyChoices(Collection<Command.Choice> choices) {
        event.replyChoices(choices).queue();
    }

}
