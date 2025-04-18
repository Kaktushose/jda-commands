package com.github.kaktushose.jda.commands.annotations.constraints;


import com.github.kaktushose.jda.commands.dispatching.adapter.impl.RoleAdapter;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// The annotated element must **not** be the specified user or member. This constraint will use the
/// [TypeAdapters] to determine the user or member.
///
/// @see Constraint
/// @see RoleAdapter
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint({Member.class, User.class})
public @interface NotUser {

    /// Returns the user or member the element must not be equals to.
    ///
    /// @return the user or member the element must not be equals to
    String value();

    /// Returns the error message that will be displayed if the constraint fails.
    ///
    /// @return the error message
    String message() default "The given Member or User is invalid as a parameter";
}
