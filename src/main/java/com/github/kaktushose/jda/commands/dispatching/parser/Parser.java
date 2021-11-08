package com.github.kaktushose.jda.commands.dispatching.parser;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.CommandDispatcher;
import net.dv8tion.jda.api.events.GenericEvent;

public abstract class Parser<T extends GenericEvent> {

    CommandContext parseInternal(GenericEvent event, CommandDispatcher dispatcher) {
        return parse((T) event, dispatcher);
    }

    public abstract CommandContext parse(T event, CommandDispatcher dispatcher);

}
