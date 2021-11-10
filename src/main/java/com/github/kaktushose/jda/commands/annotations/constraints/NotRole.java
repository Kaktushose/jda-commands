package com.github.kaktushose.jda.commands.annotations.constraints;

import net.dv8tion.jda.api.entities.Member;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be member that <b>doesn't</b> have the specified guild role. This constraint will use the
 * {@link com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry} to determine the role.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see Constraint
 * @see com.github.kaktushose.jda.commands.dispatching.adapter.impl.RoleAdapter
 * @since 2.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint({Member.class})
public @interface NotRole {

    /**
     * Returns the guild role the element must not have.
     *
     * @return the guild role the element must not have.
     */
    String value();

    /**
     * Returns the error message that will be displayed if the constraint fails.
     *
     * @return the error message
     */
    String message() default "";
}
