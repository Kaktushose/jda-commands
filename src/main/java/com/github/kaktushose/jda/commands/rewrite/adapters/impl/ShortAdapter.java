package com.github.kaktushose.jda.commands.rewrite.adapters.impl;

import com.github.kaktushose.jda.commands.rewrite.adapters.ParameterAdapter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;

import java.util.Optional;

public class ShortAdapter implements ParameterAdapter<Short> {

    @Override
    public Optional<Short> parse(String raw, CommandContext context) {
        try {
            return Optional.of(Short.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
