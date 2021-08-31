package com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.ParameterAdapter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;

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
