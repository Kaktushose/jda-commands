package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/// Type adapter for JDAs [Member].
public class MemberAdapter implements TypeAdapter<Member> {

    /// Attempts to parse a String to a [Member]. Accepts both the member id and name.
    ///
    /// @param raw   the String to parse
    /// @param event the [GenericInteractionCreateEvent]
    /// @return the parsed [Member] or an empty Optional if the parsing fails
    @NotNull
    @Override
    public Optional<Member> apply(@NotNull String raw, @NotNull GenericInteractionCreateEvent event) {
        if (event.getGuild() == null) {
            return Optional.empty();
        }

        Member member;
        raw = Helpers.sanitizeMention(raw);

        Guild guild = event.getGuild();
        Helpers.checkDetached(guild, MemberAdapter.class);

        if (raw.matches("\\d+")) {
            try {
                member = guild.retrieveMemberById(raw).complete();
            } catch (ErrorResponseException ignored) {
                member = null;
            }
        } else {
            member = guild.getMembersByEffectiveName(raw, true).stream().findFirst().orElse(null);
        }
        if (member == null) {
            return Optional.empty();
        }
        return Optional.of(member);
    }

}
