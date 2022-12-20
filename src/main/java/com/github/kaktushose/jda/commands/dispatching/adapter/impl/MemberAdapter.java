package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for JDAs {@link Member}.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public class MemberAdapter implements TypeAdapter<Member> {

    /**
     * Attempts to parse a String to a {@link Member}. Accepts both the member id and name.
     *
     * @param raw     the String to parse
     * @param context the {@link CommandContext}
     * @return the parsed {@link Member} or an empty Optional if the parsing fails
     */
    @Override
    public Optional<Member> parse(@NotNull String raw, @NotNull CommandContext context) {
        if (!context.getEvent().isFromType(ChannelType.TEXT)) {
            return Optional.empty();
        }

        Member member;
        raw = sanitizeMention(raw);

        Guild guild = context.getEvent().getGuild();
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
