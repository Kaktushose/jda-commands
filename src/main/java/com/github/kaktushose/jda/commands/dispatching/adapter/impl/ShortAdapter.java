package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;

import java.util.Optional;

public class ShortAdapter implements TypeAdapter<Short> {

    @Override
    public Optional<Short> parse(String raw, CommandContext context) {
        try {
            return Optional.of(Short.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
