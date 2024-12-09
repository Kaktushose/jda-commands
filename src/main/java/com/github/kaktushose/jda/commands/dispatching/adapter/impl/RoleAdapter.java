package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.Helpers;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.refactor.context.ExecutionContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for JDAs {@link Role}.
 *
 * @since 2.0.0
 */
public class RoleAdapter implements TypeAdapter<Role> {

    /**
     * Attempts to parse a String to a {@link Role}. Accepts both the role id and name.
     *
     * @param raw     the String to parse
     * @param context the {@link Context}
     * @return the parsed {@link Role} or an empty Optional if the parsing fails
     */
    @Override
    public Optional<Role> apply(@NotNull String raw, @NotNull ExecutionContext<?, ?> context) {
        if (context.event().getGuild() == null) {
            return Optional.empty();
        }

        Role role;
        raw = Helpers.sanitizeMention(raw);

        Guild guild = context.event().getGuild();
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
