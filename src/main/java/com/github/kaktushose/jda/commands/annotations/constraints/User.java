package com.github.kaktushose.jda.commands.annotations.constraints;

import net.dv8tion.jda.api.entities.Member;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be the specified user or member. This constraint will use the
 * {@link com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry TypeAdapterRegistry} to determine
 * the user or member.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see Constraint
 * @see com.github.kaktushose.jda.commands.dispatching.adapter.impl.RoleAdapter RoleAdapter
 * @since 2.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint({Member.class, net.dv8tion.jda.api.entities.User.class})
public @interface User {

    /**
     * Returns the user or member the element must be equals to.
     *
     * @return the user or member the element must be equals to
     */
    String value();

    /**
     * Returns the error message that will be displayed if the constraint fails.
     *
     * @return the error message
     */
    String message() default "";
}
