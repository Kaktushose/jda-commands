package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for double values.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public class DoubleAdapter implements TypeAdapter<Double> {

    /**
     * Attempts to parse a String to a Double.
     *
     * @param raw     the String to parse
     * @param context the {@link CommandContext}
     * @return the parsed Double or an empty Optional if the parsing fails
     */
    @Override
    public Optional<Double> parse(@NotNull String raw, @NotNull CommandContext context) {
        try {
            return Optional.of(Double.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
