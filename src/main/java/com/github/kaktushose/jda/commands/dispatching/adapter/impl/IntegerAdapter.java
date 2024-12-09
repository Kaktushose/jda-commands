package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.refactor.context.ExecutionContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for integer values.
 *
 * @since 2.0.0
 */
public class IntegerAdapter implements TypeAdapter<Integer> {

    /**
     * Attempts to parse a String to an Integer.
     *
     * @param raw     the String to parse
     * @param context the {@link Context}
     * @return the parsed Integer or an empty Optional if the parsing fails
     */
    @Override
    public Optional<Integer> apply(@NotNull String raw, @NotNull ExecutionContext<?, ?> context) {
        try {
            return Optional.of((int) Double.parseDouble(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

}
