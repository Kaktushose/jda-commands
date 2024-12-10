package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for double values.
 *
 * @since 2.0.0
 */
public class DoubleAdapter implements TypeAdapter<Double> {

    /**
     * Attempts to parse a String to a Double.
     *
     * @param raw   the String to parse
     * @param event the {@link Context}
     * @return the parsed Double or an empty Optional if the parsing fails
     */
    @Override
    public Optional<Double> apply(@NotNull String raw, @NotNull GenericInteractionCreateEvent event) {
        try {
            return Optional.of(Double.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
