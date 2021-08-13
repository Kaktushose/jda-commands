package com.github.kaktushose.jda.commands.rewrite.parameter.adapter.impl;

import com.github.kaktushose.jda.commands.rewrite.parameter.adapter.ParameterAdapter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.Optional;

public class MemberAdapter implements ParameterAdapter<Member> {

    @Override
    public Optional<Member> parse(String raw, Guild guild) {
        Member member;
        if (raw.matches("\\d+")) {
            member = guild.getMemberById(raw);
        } else {
            member = guild.getMembersByName(raw, true).stream().findFirst().orElse(null);
        }
        if (member == null) {
            return Optional.empty();
        }
        return Optional.of(member);
    }

}
