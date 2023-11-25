package com.github.kaktushose.jda.commands.annotations.constraints;

import net.dv8tion.jda.api.entities.Member;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be member that have the specified guild role. This constraint will use the
 * {@link com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry} to determine the role.
 *
 * @see Constraint
 * @see com.github.kaktushose.jda.commands.dispatching.adapter.impl.RoleAdapter RoleAdapter
 * @since 2.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint({Member.class})
public @interface Role {

    /**
     * Returns the guild role the element must have.
     *
     * @return the guild role the element must have.
     */
    String value();

    /**
     * Returns the error message that will be displayed if the constraint fails.
     *
     * @return the error message
     */
    String message() default "Member or User is missing at least one role that is required";
}
