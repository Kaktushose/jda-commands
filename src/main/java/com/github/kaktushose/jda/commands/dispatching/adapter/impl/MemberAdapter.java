package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.Optional;

public class MemberAdapter implements TypeAdapter<Member> {

    @Override
    public Optional<Member> parse(String raw, CommandContext context) {
        Member member;
        raw = sanitizeMention(raw);
        Guild guild = context.getEvent().getGuild();
        if (raw.matches("\\d+")) {
            member = guild.retrieveMemberById(raw).complete();
        } else {
            member = guild.getMembersByName(raw, true).stream().findFirst().orElse(null);
        }
        if (member == null) {
            return Optional.empty();
        }
        return Optional.of(member);
    }

}
