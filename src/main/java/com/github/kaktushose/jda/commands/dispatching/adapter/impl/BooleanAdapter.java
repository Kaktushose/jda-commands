package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.ParameterAdapter;

import java.util.Optional;

public class BooleanAdapter implements ParameterAdapter<Boolean> {

    @Override
    public Optional<Boolean> parse(String raw, CommandContext context) {
        if ("true".equalsIgnoreCase(raw) || "1".equals(raw)) {
            return Optional.of(true);
        }
        if ("false".equalsIgnoreCase(raw) || "0".equals(raw)) {
            return Optional.of(false);
        }
        return Optional.empty();
    }
}
