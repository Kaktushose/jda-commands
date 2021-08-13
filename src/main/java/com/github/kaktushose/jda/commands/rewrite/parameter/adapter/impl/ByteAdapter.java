package com.github.kaktushose.jda.commands.rewrite.parameter.adapter.impl;

import com.github.kaktushose.jda.commands.rewrite.parameter.adapter.ParameterAdapter;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Optional;

public class ByteAdapter implements ParameterAdapter<Byte> {

    @Override
    public Optional<Byte> parse(String raw, Guild guild) {
        try {
            return Optional.of(Byte.valueOf(raw));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
