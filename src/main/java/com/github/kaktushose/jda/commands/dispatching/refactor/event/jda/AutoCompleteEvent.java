package com.github.kaktushose.jda.commands.dispatching.refactor.event.jda;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public record AutoCompleteEvent(CommandAutoCompleteInteractionEvent event) implements JDAEvent {
}
