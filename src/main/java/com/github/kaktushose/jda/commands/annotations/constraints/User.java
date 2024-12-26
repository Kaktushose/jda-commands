package com.github.kaktushose.jda.commands.annotations.constraints;

import net.dv8tion.jda.api.entities.Member;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// The annotated element must be the specified user or member. This constraint will use the
/// [TypeAdapterRegistry][com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry] to determine
/// the user or member.
///
/// @see Constraint
/// @see com.github.kaktushose.jda.commands.dispatching.adapter.impl.RoleAdapter RoleAdapter
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint({Member.class, net.dv8tion.jda.api.entities.User.class})
public @interface User {

    /// Returns the user or member the element must be equals to.
    ///
    /// @return the user or member the element must be equals to
    String value();

    /// Returns the error message that will be displayed if the constraint fails.
    ///
    /// @return the error message
    String message() default "The given Member or User is invalid as a parameter";
}
