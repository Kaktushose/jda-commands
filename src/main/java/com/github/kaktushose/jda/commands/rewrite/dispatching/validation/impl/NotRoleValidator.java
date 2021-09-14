package com.github.kaktushose.jda.commands.rewrite.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.rewrite.annotations.constraints.NotRole;
import com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.impl.RoleAdapter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.rewrite.dispatching.validation.Validator;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.Optional;

public class NotRoleValidator implements Validator {
    @Override
    public boolean validate(Object argument, Object annotation, CommandContext context) {
        NotRole roleAnnotation = (NotRole) annotation;

        Optional<Role> optional = new RoleAdapter().parse(roleAnnotation.value(), context);
        Member member = (Member) argument;

        return !optional.filter(role -> member.getRoles().contains(role)).isPresent();
    }
}