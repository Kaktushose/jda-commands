package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.Perm;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/// A [Validator] implementation that checks the [Perm] constraint.
///
/// @see Perm
public class PermissionValidator implements Validator<Member, Perm> {

    /// Validates an argument. The argument must be a user or member that has the specified discord permission.
    ///
    /// @param context    the corresponding [InvocationContext]
    /// permission
    @Override
    public void apply(@NotNull Member member, @NotNull Perm perm, @NotNull Context context) {
        Set<Permission> permissions = new HashSet<>();
        try {
            for (String permission : perm.value()) {
                permissions.add(Permission.valueOf(permission));
            }
        } catch (IllegalArgumentException ignored) {
            return;
        }


        Helpers.checkDetached(member, PermissionValidator.class);
        if (!member.hasPermission(permissions)) {
            context.cancel("validator.perm.fail@Member is missing at least one permission that is required");
        }
    }
}
