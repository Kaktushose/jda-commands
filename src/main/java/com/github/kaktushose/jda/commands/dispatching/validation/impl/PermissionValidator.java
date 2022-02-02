package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.Perm;
import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

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
    public boolean validate(@NotNull Object argument, @NotNull Object annotation, @NotNull CommandContext context) {
        Set<Permission> permissions = new HashSet<>();
        Perm perm = (Perm) annotation;
        try {
            for (String permission : perm.value()) {
                permissions.add(Permission.valueOf(permission));
            }
        } catch (IllegalArgumentException ignored) {
            return false;
        }

        if (!Member.class.isAssignableFrom(argument.getClass())) {
            throw new IllegalArgumentException("The default PermissionValidator does only support parameters of type Member!");
        }

        Member member = (Member) argument;
        return member.hasPermission(permissions);
    }
}
