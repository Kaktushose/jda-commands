package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.Perm;
import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

//TODO support own permission system
public class PermissionValidator implements Validator {

    @Override
    public boolean validate(Object argument, Object annotation, CommandContext context) {
        Permission permission;
        try {
            Perm perm = (Perm) annotation;
            permission = Permission.valueOf(perm.value());
        } catch (IllegalArgumentException ignored) {
            return false;
        }
        Member member = (Member) argument;
        return member.hasPermission(permission);
    }
}
