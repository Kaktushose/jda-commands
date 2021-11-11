package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;

import java.util.Optional;

/**
 * Type adapter for boolean values.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public class BooleanAdapter implements TypeAdapter<Boolean> {

    /**
     * Attempts to parse a String to a Boolean. Accepts both String literals and {@code 0} or {@code 1}.
     * Parsing is <em>case-insensitive</em>.
     *
     * @param raw     the String to parse
     * @param context the {@link CommandContext}
     * @return the parsed boolean or an empty Optional if the parsing fails
     */
    @Override
    public Optional<Boolean> parse(String raw, CommandContext context) {
        if ("true".equalsIgnoreCase(raw) || "1".equals(raw)) {
            return Optional.of(true);
        }
        if ("false".equalsIgnoreCase(raw) || "0".equals(raw)) {
            return Optional.of(false);
        }
        return Optional.empty();
    }
}
