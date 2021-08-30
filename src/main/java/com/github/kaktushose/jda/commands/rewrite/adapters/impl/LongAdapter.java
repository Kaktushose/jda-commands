package com.github.kaktushose.jda.commands.rewrite.adapters.impl;

import com.github.kaktushose.jda.commands.rewrite.adapters.ParameterAdapter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;

import java.util.Optional;

public class LongAdapter implements ParameterAdapter<Long> {

    @Override
    public Optional<Long> parse(String raw, CommandContext context) {
        try {
            return Optional.of(Long.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
