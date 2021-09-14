package com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.ParameterAdapter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import java.util.Optional;

public class UserAdapter implements ParameterAdapter<User> {

    @Override
    public Optional<User> parse(String raw, CommandContext context) {
        User user;
        raw = sanitizeMention(raw);
        JDA jda = context.getEvent().getJDA();
        if (raw.matches("\\d+")) {
            user = jda.retrieveUserById(raw).complete();
        } else {
            user = jda.getUsersByName(raw, true).stream().findFirst().orElse(null);
        }
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(user);
    }
}