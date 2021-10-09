package com.github.kaktushose.jda.commands.dispatching.adapter;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;

import java.util.Optional;

public interface TypeAdapter<T> {

    Optional<T> parse(String raw, CommandContext context);

    default String sanitizeMention(String mention) {
        if (mention.matches("<[@#][&!]?([0-9]{4,})>")) {
            return mention.replaceAll("<[@#][&!]?", "").replace(">", "");
        }
        return mention;
    }

}
