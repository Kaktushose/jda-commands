package com.github.kaktushose.jda.commands.dispatching.parser;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import net.dv8tion.jda.api.events.GenericEvent;

public abstract class Parser<T extends GenericEvent> {

    CommandContext parseInternal(GenericEvent event) {
        return parse((T) event);
    }

    public abstract CommandContext parse(T event);

}
