package com.github.kaktushose.jda.commands.annotations.constraints;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.adapter.impl.RoleAdapter;
import net.dv8tion.jda.api.entities.Member;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// The annotated element must be member that have the specified guild role. This constraint will use the
/// [TypeAdapterRegistry] to determine the role.
///
/// @see Constraint
/// @see RoleAdapter
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint({Member.class})
public @interface Role {

    /// Returns the guild role the element must have.
    ///
    /// @return the guild role the element must have.
    String value();

    /// Returns the error message that will be displayed if the constraint fails.
    ///
    /// @return the error message
    String message() default "Member or User is missing at least one role that is required";
}
