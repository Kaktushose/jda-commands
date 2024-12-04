package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for float values.
 *
 * @since 2.0.0
 */
public class FloatAdapter implements TypeAdapter<Float> {

    /**
     * Attempts to parse a String to a Float.
     *
     * @param raw     the String to parse
     * @param context the {@link Context}
     * @return the parsed Float or an empty Optional if the parsing fails
     */
    @Override
    public Optional<Float> apply(@NotNull String raw, @NotNull Context context) {
        try {
            return Optional.of(Float.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

}
