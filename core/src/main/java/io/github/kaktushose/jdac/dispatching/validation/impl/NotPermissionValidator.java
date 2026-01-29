package io.github.kaktushose.jdac.dispatching.validation.impl;

import io.github.kaktushose.jdac.annotations.constraints.NotPerm;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.dispatching.validation.Validator;
import io.github.kaktushose.jdac.internal.Helpers;
import net.dv8tion.jda.api.entities.Member;

/// A [Validator] implementation that checks the [NotPerm] constraint.
///
/// @see NotPerm
public class NotPermissionValidator implements Validator<Member, NotPerm> {

    /// Validates an argument. The argument must be a [Member] that **doesn't** have the specified discord
    /// permission.
    ///
    /// @param context the corresponding [InvocationContext]
    /// @param member  the [Member] to check
    /// @param perm    the [NotPerm] to use
    @Override
    public void apply(Member member, NotPerm perm, Context context) {
        Helpers.checkDetached(member, NotPermissionValidator.class);
        if (member.hasPermission(perm.value())) {
            context.fail("jdac$member-has-unallowed-permission");
        }
    }
}
