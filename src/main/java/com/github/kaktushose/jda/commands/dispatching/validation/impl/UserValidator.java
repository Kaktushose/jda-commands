package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.User;
import com.github.kaktushose.jda.commands.dispatching.adapter.impl.MemberAdapter;
import com.github.kaktushose.jda.commands.dispatching.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/// A [Validator] implementation that checks the [User] constraint.
///
/// @see User
public class UserValidator implements Validator {

    /// Validates an argument. The argument must be the specified user or member.
    ///
    /// @param argument   the argument to validate
    /// @param annotation the corresponding annotation
    /// @param context    the corresponding [InvocationContext]
    /// @return `true` if the argument is the specified user or member
    @Override
    public boolean apply(@NotNull Object argument, @NotNull Object annotation, @NotNull InvocationContext<?> context) {
        Member member = (Member) argument;
        User user = (User) annotation;
        Optional<Member> optional = new MemberAdapter().apply(user.value(), context.event());
        return optional.filter(member::equals).isPresent();
    }
}
