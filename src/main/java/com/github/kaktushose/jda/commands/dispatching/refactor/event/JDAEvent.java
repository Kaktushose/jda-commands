package com.github.kaktushose.jda.commands.dispatching.refactor.event;

import com.github.kaktushose.jda.commands.dispatching.refactor.event.jda.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.dispatching.refactor.event.jda.CommandEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public sealed interface JDAEvent<T extends GenericInteractionCreateEvent> permits AutoCompleteEvent, CommandEvent {
    T event();
}
