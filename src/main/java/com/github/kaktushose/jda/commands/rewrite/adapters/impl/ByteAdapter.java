package com.github.kaktushose.jda.commands.rewrite.adapters.impl;

import com.github.kaktushose.jda.commands.rewrite.adapters.ParameterAdapter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;

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
