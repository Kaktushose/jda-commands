package com.github.kaktushose.jda.commands.dispatching.interactions;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandDispatcher;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract toplevel class for defining event parsers.
 *
 * @param <T> a subtype of {@link GenericEvent} the parser can parse
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public abstract class GenericParser<T extends GenericEvent> {

    public GenericContext<? extends GenericInteractionCreateEvent> parseInternal(GenericEvent event, JDACommands jdaCommands) {
        return parse((T) event, jdaCommands);
    }

    /**
     * Takes a subtype of {@link GenericEvent}, parses and transpiles it into a {@link GenericContext}.
     *
     * @param event      the subtype of {@link GenericEvent}
     * @param dispatcher the calling {@link CommandDispatcher}
     * @return a new {@link GenericContext}
     */
    @NotNull
    public abstract GenericContext<? extends GenericInteractionCreateEvent> parse(@NotNull T event, @NotNull JDACommands jdaCommands);

}
