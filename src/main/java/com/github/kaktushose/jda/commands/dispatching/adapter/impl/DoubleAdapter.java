package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;

import java.util.Optional;

public class DoubleAdapter implements TypeAdapter<Double> {

    @Override
    public Optional<Double> parse(String raw, CommandContext context) {
        try {
            return Optional.of(Double.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
