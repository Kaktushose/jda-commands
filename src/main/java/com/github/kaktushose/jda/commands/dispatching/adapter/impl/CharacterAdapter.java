package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/// Type adapter for character values.
public class CharacterAdapter implements TypeAdapter<Character> {

    /// Casts a String to a Char if and only if `raw.length == 1`. Else, returns an empty Optional.
    ///
    /// @param raw   the String to parse
    /// @param event the [GenericInteractionCreateEvent]
    /// @return the parsed Char or an empty Optional if the parsing fails
    @NotNull
    @Override
    public Optional<Character> apply(@NotNull String raw, @NotNull GenericInteractionCreateEvent event) {
        if (raw.length() == 1) {
            return Optional.of(raw.charAt(0));
        }
        return Optional.empty();
    }
}
