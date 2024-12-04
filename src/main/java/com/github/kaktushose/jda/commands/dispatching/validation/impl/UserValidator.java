package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.User;
import com.github.kaktushose.jda.commands.dispatching.adapter.impl.MemberAdapter;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A {@link Validator} implementation that checks the {@link User} constraint.
 *
 * @see User
 * @since 2.0.0
 */
public class UserValidator implements Validator {

    /**
     * Validates an argument. The argument must be the specified user or member.
     *
     * @param argument   the argument to validate
     * @param annotation the corresponding annotation
     * @param context    the corresponding {@link Context}
     * @return {@code true} if the argument is the specified user or member
     */
    @Override
    public boolean apply(@NotNull Object argument, @NotNull Object annotation, @NotNull Context context) {
        Member member = (Member) argument;
        User user = (User) annotation;
        Optional<Member> optional = new MemberAdapter().apply(user.value(), context);
        return optional.filter(member::equals).isPresent();
    }
}
