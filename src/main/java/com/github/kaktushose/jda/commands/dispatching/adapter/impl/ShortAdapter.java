package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for short values.
 *
 * @since 2.0.0
 */
public class ShortAdapter implements TypeAdapter<Short> {

    /**
     * Attempts to parse a String to a Short.
     *
     * @param raw     the String to parse
     * @param context the {@link Context}
     * @return the parsed Short or an empty Optional if the parsing fails
     */
    @Override
    public Optional<Short> parse(@NotNull String raw, @NotNull Context context) {
        try {
            return Optional.of(Short.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
