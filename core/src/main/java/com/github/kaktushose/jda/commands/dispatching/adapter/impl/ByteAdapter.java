package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import java.util.Optional;

/// Type adapter for byte values.
public class ByteAdapter implements TypeAdapter<Byte> {

    /// Attempts to parse a String to a Byte.
    ///
    /// @param raw   the String to parse
    /// @param event the [GenericInteractionCreateEvent]
    /// @return the parsed Byte or an empty Optional if the parsing fails
    
    @Override
    public Optional<Byte> apply(String raw, GenericInteractionCreateEvent event) {
        try {
            return Optional.of(Byte.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
