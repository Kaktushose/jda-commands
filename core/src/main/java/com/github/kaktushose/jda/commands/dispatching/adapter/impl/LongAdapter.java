package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import java.util.Optional;

/// Type adapter for long values.
public class LongAdapter implements TypeAdapter<Long> {

    /// Attempts to parse a String to a Long.
    ///
    /// @param raw   the String to parse
    /// @param event the [GenericInteractionCreateEvent]
    /// @return the parsed Long or an empty Optional if the parsing fails
    
    @Override
    public Optional<Long> apply(String raw, GenericInteractionCreateEvent event) {
        try {
            return Optional.of((long) Double.parseDouble(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

}
