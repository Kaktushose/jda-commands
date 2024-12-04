package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
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
     * @param raw     the String to parse
     * @param context the {@link Context}
     * @return the parsed Double or an empty Optional if the parsing fails
     */
    @Override
    public Optional<Double> apply(@NotNull String raw, @NotNull Context context) {
        try {
            return Optional.of(Double.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
