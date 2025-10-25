package com.github.kaktushose.jda.commands.annotations.constraints;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// The annotated element must be a user or member that **doesn't** have the specified discord permission.
///
/// @see Constraint
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint({Member.class})
public @interface NotPerm {

    /// Returns the discord permission(s) the element must not have.
    ///
    /// @return the discord permission(s) the element must not have.
    Permission[] value();
}
