package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import java.util.Optional;

/// Type adapter for integer values.
public class IntegerAdapter implements TypeAdapter<Integer> {

    /// Attempts to parse a String to an Integer.
    ///
    /// @param raw   the String to parse
    /// @param event the [GenericInteractionCreateEvent]
    /// @return the parsed Integer or an empty Optional if the parsing fails
    
    @Override
    public Optional<Integer> apply(String raw, GenericInteractionCreateEvent event) {
        try {
            return Optional.of((int) Double.parseDouble(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

}
