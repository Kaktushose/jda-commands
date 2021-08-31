package com.github.kaktushose.jda.commands.rewrite.dispatching.adapter;

import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;

import java.util.Optional;

public interface ParameterAdapter<T> {

    Optional<T> parse(String raw, CommandContext context);

    default String formatMention(String mention) {
        if (mention.matches("<[@#][&!]?([0-9]{4,})>")) {
            return mention.replaceAll("<[@#][&!]?", "").replace(">", "");
        }
        return mention;
    }

}
