package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.NotPerm;
import com.github.kaktushose.jda.commands.dispatching.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/// A [Validator] implementation that checks the [NotPerm] constraint.
///
/// @see NotPerm
public class NotPermissionValidator implements Validator {

    /// Validates an argument. The argument must be a user or member that **doesn't** have the specified discord
    /// permission.
    ///
    /// @param argument   the argument to validate
    /// @param annotation the corresponding annotation
    /// @param context    the corresponding [InvocationContext]
    /// @return `true` if the argument is a user or member that **doesn't** have the specified discord
    /// permission
    @Override
    public boolean apply(@NotNull Object argument, @NotNull Object annotation, @NotNull InvocationContext<?> context) {
        Set<Permission> permissions = new HashSet<>();
        NotPerm perm = (NotPerm) annotation;

        try {
            for (String permission : perm.value()) {
                permissions.add(Permission.valueOf(permission));
            }
        } catch (IllegalArgumentException ignored) {
            return true;
        }

        if (!Member.class.isAssignableFrom(argument.getClass())) {
            throw new IllegalArgumentException("The default NotPermissionValidator does only support parameters of type Member!");
        }

        Member member = (Member) argument;
        return !member.hasPermission(permissions);
    }
}
