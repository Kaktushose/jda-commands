package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.impl.RoleAdapter;
import com.github.kaktushose.jda.commands.dispatching.refactor.ExecutionContext;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A {@link Validator} implementation that checks the
 * {@link com.github.kaktushose.jda.commands.annotations.constraints.Role Role} constraint.
 *
 * @see com.github.kaktushose.jda.commands.annotations.constraints.Role Role
 * @since 2.0.0
 */
public class RoleValidator implements Validator {

    /**
     * Validates an argument. The argument must be a user or member that has the specified guild role.
     *
     * @param argument   the argument to validate
     * @param annotation the corresponding annotation
     * @param context    the corresponding {@link Context}
     * @return {@code true} if the argument is a user or member that has the specified guild role
     */
    @Override
    public boolean apply(@NotNull Object argument, @NotNull Object annotation, @NotNull ExecutionContext<?, ?> context) {
        com.github.kaktushose.jda.commands.annotations.constraints.Role roleAnnotation =
                (com.github.kaktushose.jda.commands.annotations.constraints.Role) annotation;

        Optional<Role> optional = new RoleAdapter().apply(roleAnnotation.value(), context.event());
        Member member = (Member) argument;

        return optional.filter(role -> member.getRoles().contains(role)).isPresent();
    }
}
