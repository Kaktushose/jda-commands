package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.NotUser;
import com.github.kaktushose.jda.commands.dispatching.adapter.impl.MemberAdapter;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/// A [Validator] implementation that checks the [NotUser] constraint.
///
/// @see NotUser
public class NotUserValidator implements Validator {

    /// Validates an argument. The argument must **not** be the specified user or member.
    ///
    /// @param argument   the argument to validate
    /// @param annotation the corresponding annotation
    /// @param context    the corresponding [InvocationContext]
    /// @return `true` if the argument **isn't** the specified user or member
    @Override
    public boolean apply(@NotNull Object argument, @NotNull Object annotation, @NotNull InvocationContext<?> context) {
        Member member = (Member) argument;
        NotUser user = (NotUser) annotation;
        Optional<Member> optional = new MemberAdapter().apply(user.value(), context.event());
        return optional.filter(member::equals).isEmpty();
    }
}
