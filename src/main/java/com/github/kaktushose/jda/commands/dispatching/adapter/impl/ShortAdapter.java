package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;

import java.util.Optional;

/**
 * Type adapter for short values.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public class ShortAdapter implements TypeAdapter<Short> {

    /**
     * Attempts to parse a String to a Short.
     *
     * @param raw     the String to parse
     * @param context the {@link CommandContext}
     * @return the parsed Short or an empty Optional if the parsing fails
     */
    @Override
    public Optional<Short> parse(String raw, CommandContext context) {
        try {
            return Optional.of(Short.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
