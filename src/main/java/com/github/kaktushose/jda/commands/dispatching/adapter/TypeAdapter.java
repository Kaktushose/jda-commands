package com.github.kaktushose.jda.commands.dispatching.adapter;

import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Generic top level interface for type adapting.
 *
 * @param <T> the type the adapter parses
 * @see com.github.kaktushose.jda.commands.annotations.Implementation
 * @since 2.0.0
 */
@FunctionalInterface
public interface TypeAdapter<T> extends BiFunction<String, GenericInteractionCreateEvent, Optional<T>> {

    /**
     * Attempts to parse a String to the given type.
     *
     * @param raw   the String to parse
     * @param event the {@link GenericInteractionCreateEvent}
     * @return the parsed type or an empty Optional if the parsing fails
     */
    Optional<T> apply(@NotNull String raw, @NotNull GenericInteractionCreateEvent event);

}
