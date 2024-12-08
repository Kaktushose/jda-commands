package com.github.kaktushose.jda.commands.dispatching.refactor.event.jda;

import com.github.kaktushose.jda.commands.dispatching.refactor.event.Event;

public sealed interface JDAEvent extends Event permits AutoCompleteEvent, CommandEvent {
}
