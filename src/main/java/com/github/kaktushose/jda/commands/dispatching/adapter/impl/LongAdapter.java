package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for long values.
 *
 * @since 2.0.0
 */
public class LongAdapter implements TypeAdapter<Long> {

    /**
     * Attempts to parse a String to a Long.
     *
     * @param raw   the String to parse
     * @param event the {@link Context}
     * @return the parsed Long or an empty Optional if the parsing fails
     */
    @Override
    public Optional<Long> apply(@NotNull String raw, @NotNull GenericInteractionCreateEvent event) {
        try {
            return Optional.of((long) Double.parseDouble(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

}
