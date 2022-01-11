package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.Perm;
import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

/**
 * A {@link Validator} implementation that checks the {@link Perm} constraint.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see Perm
 * @since 2.0.0
 */
public class PermissionValidator implements Validator {

    /**
     * Validates an argument. The argument must be a user or member that has the specified discord
     * permission.
     *
     * @param argument   the argument to validate
     * @param annotation the corresponding annotation
     * @param context    the corresponding {@link CommandContext}
     * @return {@code true} if the argument is a user or member that has the specified discord
     * permission
     */
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
