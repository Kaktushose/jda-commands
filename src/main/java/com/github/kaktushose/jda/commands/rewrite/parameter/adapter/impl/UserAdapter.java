package com.github.kaktushose.jda.commands.rewrite.parameter.adapter.impl;

import com.github.kaktushose.jda.commands.rewrite.parameter.adapter.ParameterAdapter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.Optional;

public class UserAdapter implements ParameterAdapter<User> {

    @Override
    public Optional<User> parse(String raw, Guild guild) {
        User user;
        if (raw.matches("\\d+")) {
            user = guild.getJDA().getUserById(raw);
        } else {
            user = guild.getJDA().getUsersByName(raw, true).stream().findFirst().orElse(null);
        }
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(user);
    }
}
