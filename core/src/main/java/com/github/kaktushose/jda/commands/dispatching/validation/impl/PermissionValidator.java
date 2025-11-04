package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.Perm;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.entities.Member;

/// A [Validator] implementation that checks the [Perm] constraint.
///
/// @see Perm
public class PermissionValidator implements Validator<Member, Perm> {

    /// Validates an argument. The argument must be a [Member] that has the specified discord permission.
    ///
    /// @param context the corresponding [InvocationContext]
    /// @param member the [Member] to check
    /// @param perm the [Perm] to use
    @Override
    public void apply(Member member, Perm perm, Context context) {
        Helpers.checkDetached(member, PermissionValidator.class);
        if (!member.hasPermission(perm.value())) {
            context.fail("Member is missing at least one permission that is required");
        }
    }
}
