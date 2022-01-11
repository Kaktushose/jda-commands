package com.github.kaktushose.jda.commands.dispatching.validation.impl;

import com.github.kaktushose.jda.commands.annotations.constraints.NotUser;
import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.impl.MemberAdapter;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import net.dv8tion.jda.api.entities.Member;

import java.util.Optional;

/**
 * A {@link Validator} implementation that checks the {@link NotUser} constraint.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see NotUser
 * @since 2.0.0
 */
public class NotUserValidator implements Validator {

    /**
     * Validates an argument. The argument must <b>not</b> be the specified user or member.
     *
     * @param argument   the argument to validate
     * @param annotation the corresponding annotation
     * @param context    the corresponding {@link CommandContext}
     * @return {@code true} if the argument <b>isn't</b> the specified user or member
     */
    @Override
    public boolean validate(Object argument, Object annotation, CommandContext context) {
        Member member = (Member) argument;
        NotUser user = (NotUser) annotation;
        Optional<Member> optional = new MemberAdapter().parse(user.value(), context);
        return !optional.filter(member::equals).isPresent();
    }
}
