package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.ParameterAdapter;

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
