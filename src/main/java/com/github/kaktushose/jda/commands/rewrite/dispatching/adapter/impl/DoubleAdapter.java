package com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.ParameterAdapter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;

import java.util.Optional;

public class DoubleAdapter implements ParameterAdapter<Double> {

    @Override
    public Optional<Double> parse(String raw, CommandContext context) {
        try {
            return Optional.of(Double.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
