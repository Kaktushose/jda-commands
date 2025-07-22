package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.NotPerm;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.util.HashSet;
import java.util.Set;

/// A [Validator] implementation that checks the [NotPerm] constraint.
///
/// @see NotPerm
public class NotPermissionValidator implements Validator<Member, NotPerm> {

    /// Validates an argument. The argument must be a user or member that **doesn't** have the specified discord
    /// permission.
    ///
    /// @param context    the corresponding [InvocationContext]
    /// permission
    @Override
    public void apply(Member member, NotPerm perm, Context context) {
        Set<Permission> permissions = new HashSet<>();

        try {
            for (String permission : perm.value()) {
                permissions.add(Permission.valueOf(permission));
            }
        } catch (IllegalArgumentException ignored) {
            return;
        }


        Helpers.checkDetached(member, NotPermissionValidator.class);
        if (member.hasPermission(permissions)) {
            context.fail("validator.noperm.fail@Member has at least one permission that isn't allowed");
        }
    }
}
