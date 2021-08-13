package com.github.kaktushose.jda.commands.rewrite.parameter.adapter;

import net.dv8tion.jda.api.entities.Guild;

import java.util.Optional;

public interface ParameterAdapter<T> {

    Optional<T> parse(String raw, Guild guild);

}
