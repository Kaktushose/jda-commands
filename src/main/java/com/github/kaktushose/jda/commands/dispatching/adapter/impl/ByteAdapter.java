package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.ParameterAdapter;

import java.util.Optional;

public class ByteAdapter implements ParameterAdapter<Byte> {

    @Override
    public Optional<Byte> parse(String raw, CommandContext context) {
        try {
            return Optional.of(Byte.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
