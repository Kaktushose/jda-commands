package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import java.util.Optional;

/// Type adapter for boolean values.
public class BooleanAdapter implements TypeAdapter<Boolean> {

    /// Attempts to parse a String to a Boolean. Accepts both String literals and `0` or `1`.
    /// Parsing is _case-insensitive_.
    ///
    /// @param raw   the String to parse
    /// @param event the [GenericInteractionCreateEvent]
    /// @return the parsed boolean or an empty Optional if the parsing fails
    
    @Override
    public Optional<Boolean> apply(String raw, GenericInteractionCreateEvent event) {
        if ("true".equalsIgnoreCase(raw) || "1".equals(raw)) {
            return Optional.of(true);
        }
        if ("false".equalsIgnoreCase(raw) || "0".equals(raw)) {
            return Optional.of(false);
        }
        return Optional.empty();
    }

}
