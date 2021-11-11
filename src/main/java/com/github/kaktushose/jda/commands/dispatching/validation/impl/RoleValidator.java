package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.impl.RoleAdapter;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.Optional;

public class RoleValidator implements Validator {

    @Override
    public boolean validate(Object argument, Object annotation, CommandContext context) {
        com.github.kaktushose.jda.commands.annotations.constraints.Role roleAnnotation = (com.github.kaktushose.jda.commands.annotations.constraints.Role) annotation;

        Optional<Role> optional = new RoleAdapter().parse(roleAnnotation.value(), context);
        Member member = (Member) argument;

        return optional.filter(role -> member.getRoles().contains(role)).isPresent();
    }
}