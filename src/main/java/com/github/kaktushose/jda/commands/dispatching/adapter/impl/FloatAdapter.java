package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.ParameterAdapter;

import java.util.Optional;

public class FloatAdapter implements ParameterAdapter<Float> {

    @Override
    public Optional<Float> parse(String raw, CommandContext context) {
        try {
            return Optional.of(Float.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
