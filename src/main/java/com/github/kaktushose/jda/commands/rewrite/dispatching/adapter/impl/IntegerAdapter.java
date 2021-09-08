package com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.ParameterAdapter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;

import java.util.Optional;

public class IntegerAdapter implements ParameterAdapter<Integer> {

    @Override
    public Optional<Integer> parse(String raw, CommandContext context) {
        try {
            return Optional.of(Integer.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
