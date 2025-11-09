package io.github.kaktushose.jdac.dispatching.validation.impl;

import io.github.kaktushose.jdac.annotations.constraints.Perm;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.dispatching.validation.Validator;
import io.github.kaktushose.jdac.internal.Helpers;
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
            context.fail("jdac$member-missing-permission");
        }
    }
}
