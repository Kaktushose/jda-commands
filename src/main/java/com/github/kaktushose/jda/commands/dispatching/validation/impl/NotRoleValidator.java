package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.NotRole;
import com.github.kaktushose.jda.commands.dispatching.adapter.impl.RoleAdapter;
import com.github.kaktushose.jda.commands.dispatching.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/// A [Validator] implementation that checks the [NotRole] constraint.
///
/// @see NotRole
public class NotRoleValidator implements Validator {

    /// Validates an argument. The argument must be a user or member that **doesn't**have the specified guild role.
    ///
    /// @param argument   the argument to validate
    /// @param annotation the corresponding annotation
    /// @param context    the corresponding [InvocationContext]
    /// @return `true` if the argument is a user or member that **doesn't** have the specified guild role
    @Override
    public boolean apply(@NotNull Object argument, @NotNull Object annotation, @NotNull InvocationContext<?> context) {
        NotRole roleAnnotation = (NotRole) annotation;

        Optional<Role> optional = new RoleAdapter().apply(roleAnnotation.value(), context.event());
        Member member = (Member) argument;

        return optional.filter(role -> member.getRoles().contains(role)).isEmpty();
    }
}
