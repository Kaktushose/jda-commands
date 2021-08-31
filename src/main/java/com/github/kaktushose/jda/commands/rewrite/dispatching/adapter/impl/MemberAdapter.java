package com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.ParameterAdapter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.Optional;

public class MemberAdapter implements ParameterAdapter<Member> {

    @Override
    public Optional<Member> parse(String raw, CommandContext context) {
        Member member;
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
