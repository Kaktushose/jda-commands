package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.NotUser;
import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.impl.MemberAdapter;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import net.dv8tion.jda.api.entities.Member;

import java.util.Optional;

public class NotUserValidator implements Validator {

    @Override
    public boolean validate(Object argument, Object annotation, CommandContext context) {
        Member member = (Member) argument;
        NotUser user = (NotUser) annotation;
        Optional<Member> optional = new MemberAdapter().parse(user.value(), context);
        return !optional.filter(member::equals).isPresent();
    }
}
