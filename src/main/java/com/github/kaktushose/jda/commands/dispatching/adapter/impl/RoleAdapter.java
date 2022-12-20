package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for JDAs {@link Role}.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public class RoleAdapter implements TypeAdapter<Role> {

    /**
     * Attempts to parse a String to a {@link Role}. Accepts both the role id and name.
     *
     * @param raw     the String to parse
     * @param context the {@link CommandContext}
     * @return the parsed {@link Role} or an empty Optional if the parsing fails
     */
    @Override
    public Optional<Role> parse(@NotNull String raw, @NotNull CommandContext context) {
        if (!context.getEvent().isFromType(ChannelType.TEXT)) {
            return Optional.empty();
        }

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
