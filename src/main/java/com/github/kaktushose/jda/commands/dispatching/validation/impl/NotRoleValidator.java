package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.NotRole;
import com.github.kaktushose.jda.commands.dispatching.adapter.impl.RoleAdapter;
import com.github.kaktushose.jda.commands.dispatching.ExecutionContext;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A {@link Validator} implementation that checks the {@link NotRole} constraint.
 *
 * @see NotRole
 * @since 2.0.0
 */
public class NotRoleValidator implements Validator {

    /**
     * Validates an argument. The argument must be a user or member that <b>doesn't</b>have the specified guild role.
     *
     * @param argument   the argument to validate
     * @param annotation the corresponding annotation
     * @param context    the corresponding {@link Context}
     * @return {@code true} if the argument is a user or member that <b>doesn't</b> have the specified guild role
     */
    @Override
    public boolean apply(@NotNull Object argument, @NotNull Object annotation, @NotNull ExecutionContext<?> context) {
        NotRole roleAnnotation = (NotRole) annotation;

        Optional<Role> optional = new RoleAdapter().apply(roleAnnotation.value(), context.event());
        Member member = (Member) argument;

        return optional.filter(role -> member.getRoles().contains(role)).isEmpty();
    }
}
