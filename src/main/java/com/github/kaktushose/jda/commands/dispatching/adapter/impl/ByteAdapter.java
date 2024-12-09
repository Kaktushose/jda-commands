package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.refactor.context.ExecutionContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for byte values.
 *
 * @since 2.0.0
 */
public class ByteAdapter implements TypeAdapter<Byte> {

    /**
     * Attempts to parse a String to a Byte.
     *
     * @param raw     the String to parse
     * @param context the {@link Context}
     * @return the parsed Byte or an empty Optional if the parsing fails
     */
    @Override
    public Optional<Byte> apply(@NotNull String raw, @NotNull ExecutionContext<?, ?> context) {
        try {
            return Optional.of(Byte.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
