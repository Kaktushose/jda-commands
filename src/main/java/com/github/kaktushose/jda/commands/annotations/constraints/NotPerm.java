package com.github.kaktushose.jda.commands.annotations.constraints;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be a user or member that <b>doesn't</b> have the specified discord permission.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see Constraint
 * @since 2.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint({Member.class, User.class})
public @interface NotPerm {

    /**
     * Returns the discord permission the element must not have.
     *
     * @return the discord permission the element must not have.
     */
    String value();

    /**
     * Returns the error message that will be displayed if the constraint fails.
     *
     * @return the error message
     */
    String message() default "Member or User has at least one permission that isn't allowed";
}
