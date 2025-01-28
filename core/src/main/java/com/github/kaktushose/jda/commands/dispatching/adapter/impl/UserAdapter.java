package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/// Type adapter for JDAs [User].
public class UserAdapter implements TypeAdapter<User> {

    /// Attempts to parse a String to a [User]. Accepts both the user id and name.
    ///
    /// @param raw   the String to parse
    /// @param event the [GenericInteractionCreateEvent]
    /// @return the parsed [User] or an empty Optional if the parsing fails
    @NotNull
    @Override
    public Optional<User> apply(@NotNull String raw, @NotNull GenericInteractionCreateEvent event) {
        User user;
        raw = Helpers.sanitizeMention(raw);
        JDA jda = event.getJDA();
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
