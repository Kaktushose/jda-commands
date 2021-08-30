package com.github.kaktushose.jda.commands.rewrite.adapters;

import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;

import java.util.Optional;

public interface ParameterAdapter<T> {

    Optional<T> parse(String raw, CommandContext context);

}
