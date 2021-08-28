package com.github.kaktushose.jda.commands.rewrite.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.User;
import com.github.kaktushose.jda.commands.rewrite.parameter.adapter.impl.MemberAdapter;
import com.github.kaktushose.jda.commands.rewrite.validation.Validator;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.Optional;

public class NotUserValidator implements Validator {

    @Override
    public boolean validate(Object argument, Object annotation, Guild guild) {
        Member member = (Member) argument;
        User user = (User) annotation;
        Optional<Member> optional = new MemberAdapter().parse(user.value(), guild);
        return !optional.filter(member::equals).isPresent();
    }
}
