package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for character values.
 *
 * @since 2.0.0
 */
public class CharacterAdapter implements TypeAdapter<Character> {

    /**
     * Casts a String to a Char if and only if {@code raw.length == 1}. Else, returns an empty Optional.
     *
     * @param raw     the String to parse
     * @param context the {@link Context}
     * @return the parsed Char or an empty Optional if the parsing fails
     */
    @Override
    public Optional<Character> apply(@NotNull String raw, @NotNull Context context) {
        if (raw.length() == 1) {
            return Optional.of(raw.charAt(0));
        }
        return Optional.empty();
    }
}
