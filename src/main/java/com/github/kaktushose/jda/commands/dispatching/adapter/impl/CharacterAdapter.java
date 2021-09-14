package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.ParameterAdapter;

import java.util.Optional;

public class CharacterAdapter implements ParameterAdapter<Character> {

    @Override
    public Optional<Character> parse(String raw, CommandContext context) {
        if (raw.length() == 1) {
            return Optional.of(raw.charAt(0));
        }
        return Optional.empty();
    }
}
