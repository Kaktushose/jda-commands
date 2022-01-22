package com.github.kaktushose.jda.commands.dispatching.parser;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.CommandDispatcher;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract toplevel class for defining event parsers.
 *
 * @param <T> a subtype of {@link GenericEvent} the parser can parse
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public abstract class Parser<T extends GenericEvent> {

    CommandContext parseInternal(GenericEvent event, CommandDispatcher dispatcher) {
        return parse((T) event, dispatcher);
    }

    /**
     * Takes a subtype of {@link GenericEvent}, parses and transpiles it into a {@link CommandContext}.
     *
     * @param event      the subtype of {@link GenericEvent}
     * @param dispatcher the calling {@link CommandDispatcher}
     * @return a new {@link CommandContext}
     */
    public abstract CommandContext parse(@NotNull T event, @NotNull CommandDispatcher dispatcher);

}
