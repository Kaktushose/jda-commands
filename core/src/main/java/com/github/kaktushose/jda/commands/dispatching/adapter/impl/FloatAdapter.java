package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/// Type adapter for float values.
public class FloatAdapter implements TypeAdapter<Float> {

    /// Attempts to parse a String to a Float.
    ///
    /// @param raw   the String to parse
    /// @param event the [GenericInteractionCreateEvent]
    /// @return the parsed Float or an empty Optional if the parsing fails
    @NotNull
    @Override
    public Optional<Float> apply(@NotNull String raw, @NotNull GenericInteractionCreateEvent event) {
        try {
            return Optional.of(Float.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

}
