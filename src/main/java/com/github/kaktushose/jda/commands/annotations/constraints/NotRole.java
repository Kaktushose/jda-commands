package com.github.kaktushose.jda.commands.annotations.constraints;

import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapterRegistry;
import net.dv8tion.jda.api.entities.Member;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// The annotated element must be member that **doesn't** have the specified guild role. This constraint will use the
/// [TypeAdapterRegistry] to determine the role.
///
/// @see Constraint
/// @see com.github.kaktushose.jda.commands.dispatching.adapter.impl.RoleAdapter RoleAdapter
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint({Member.class})
public @interface NotRole {

    /// Returns the guild role the element must not have.
    ///
    /// @return the guild role the element must not have.
    String value();

    /// Returns the error message that will be displayed if the constraint fails.
    ///
    /// @return the error message
    String message() default "Member has at least one role that isn't allowed";
}
