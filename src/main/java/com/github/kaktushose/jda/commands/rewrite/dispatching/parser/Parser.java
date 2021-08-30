package com.github.kaktushose.jda.commands.rewrite.dispatching.parser;

import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;
import net.dv8tion.jda.api.events.GenericEvent;

public abstract class Parser<T extends GenericEvent> {

    void parseInternal(GenericEvent event) {
        parse((T) event);
    }

    public abstract CommandContext parse(T event);

}
