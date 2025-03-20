package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.impl.RoleAdapter;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/// A [Validator] implementation that checks the
/// [Role][com.github.kaktushose.jda.commands.annotations.constraints.Role] constraint.
///
/// @see com.github.kaktushose.jda.commands.annotations.constraints.Role Role
public class RoleValidator implements Validator {

    /// Validates an argument. The argument must be a user or member that has the specified guild role.
    ///
    /// @param argument   the argument to validate
    /// @param annotation the corresponding annotation
    /// @param context    the corresponding [InvocationContext]
    /// @return `true` if the argument is a user or member that has the specified guild role
    @Override
    public boolean apply(@NotNull Object argument, @NotNull Object annotation, @NotNull InvocationContext<?> context) {
        com.github.kaktushose.jda.commands.annotations.constraints.Role roleAnnotation =
                (com.github.kaktushose.jda.commands.annotations.constraints.Role) annotation;

        Optional<Role> optional = new RoleAdapter().apply(roleAnnotation.value(), context.event());
        Member member = (Member) argument;
        Helpers.checkDetached(member, RoleValidator.class);
        return optional.filter(role -> member.getRoles().contains(role)).isPresent();
    }
}
