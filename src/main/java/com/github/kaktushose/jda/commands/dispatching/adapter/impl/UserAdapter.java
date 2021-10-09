package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import java.util.Optional;

public class UserAdapter implements TypeAdapter<User> {

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
