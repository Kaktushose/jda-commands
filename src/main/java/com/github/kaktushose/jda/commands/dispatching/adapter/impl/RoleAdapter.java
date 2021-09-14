package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.ParameterAdapter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.Optional;

public class RoleAdapter implements ParameterAdapter<Role> {

    @Override
    public Optional<Role> parse(String raw, CommandContext context) {
        Role role;
        raw = sanitizeMention(raw);
        Guild guild = context.getEvent().getGuild();
        if (raw.matches("\\d+")) {
            role = guild.getRoleById(raw);
        } else {
            role = guild.getRolesByName(raw, true).stream().findFirst().orElse(null);
        }
        if (role == null) {
            return Optional.empty();
        }
        return Optional.of(role);
    }
}
