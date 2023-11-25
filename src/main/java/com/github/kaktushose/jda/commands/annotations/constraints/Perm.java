package com.github.kaktushose.jda.commands.annotations.constraints;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be a user or member that have the specified discord permission.
 *
 * @see Constraint
 * @since 2.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint({Member.class, User.class})
public @interface Perm {

    /**
     * Returns the discord permission(s) the element must have.
     *
     * @return the discord permission(s) the element must have.
     */
    String[] value();

    /**
     * Returns the error message that will be displayed if the constraint fails.
     *
     * @return the error message
     */
    String message() default "Member or User is missing at least one permission that is required";
}
