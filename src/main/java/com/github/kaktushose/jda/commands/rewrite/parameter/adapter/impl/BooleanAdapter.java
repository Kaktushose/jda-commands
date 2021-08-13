package com.github.kaktushose.jda.commands.rewrite.parameter.adapter.impl;

import com.github.kaktushose.jda.commands.rewrite.parameter.adapter.ParameterAdapter;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Optional;

public class BooleanAdapter implements ParameterAdapter<Boolean> {

    @Override
    public Optional<Boolean> parse(String raw, Guild guild) {
        if ("true".equalsIgnoreCase(raw) || "1".equals(raw)) {
            return Optional.of(true);
        }
        if ("false".equalsIgnoreCase(raw) || "0".equals(raw)) {
            return Optional.of(false);
        }
        return Optional.empty();
    }
}
