package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.Helpers;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for JDAs {@link User}.
 *
 * @since 2.0.0
 */
public class UserAdapter implements TypeAdapter<User> {

    /**
     * Attempts to parse a String to a {@link User}. Accepts both the user id and name.
     *
     * @param raw     the String to parse
     * @param context the {@link Context}
     * @return the parsed {@link User} or an empty Optional if the parsing fails
     */
    @Override
    public Optional<User> apply(@NotNull String raw, @NotNull Context context) {
        User user;
        raw = Helpers.sanitizeMention(raw);
        JDA jda = context.getEvent().getJDA();
        if (raw.matches("\\d+")) {
            try {
                user = jda.retrieveUserById(raw).complete();
            } catch (ErrorResponseException ignored) {
                user = null;
            }
        } else {
            user = jda.getUsersByName(raw, true).stream().findFirst().orElse(null);
        }
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(user);
    }
}
