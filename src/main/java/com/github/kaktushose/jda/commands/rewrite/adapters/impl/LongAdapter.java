package com.github.kaktushose.jda.commands.rewrite.adapters.impl;

import com.github.kaktushose.jda.commands.rewrite.adapters.ParameterAdapter;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Optional;

public class LongAdapter implements ParameterAdapter<Long> {

    @Override
    public Optional<Long> parse(String raw, Guild guild) {
        try {
            return Optional.of(Long.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
