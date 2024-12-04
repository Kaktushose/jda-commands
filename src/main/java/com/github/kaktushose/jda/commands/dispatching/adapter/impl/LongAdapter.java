package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
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
     * @param raw     the String to parse
     * @param context the {@link Context}
     * @return the parsed Long or an empty Optional if the parsing fails
     */
    @Override
    public Optional<Long> apply(@NotNull String raw, @NotNull Context context) {
        try {
            return Optional.of((long) Double.parseDouble(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

}
