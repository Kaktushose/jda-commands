package com.github.kaktushose.jda.commands.annotations;

import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.dispatching.internal.ImplementationRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;

import java.lang.annotation.*;

/// Indicates that the annotated class is a custom implementation that should replace the default implementation.
///
/// @see ImplementationRegistry ImplementationRegistry
/// @see Middleware Middleware
/// @see Validator Validator
/// @see com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter TypeAdapter
/// @see com.github.kaktushose.jda.commands.permissions.PermissionsProvider PermissionsProvider
/// @see com.github.kaktushose.jda.commands.scope.GuildScopeProvider GuildScopeProvider
/// @see ErrorMessageFactory ErrorMessageFactory
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Implementation {

    /// Gets the [Priority] to register the [Middleware] with. If this implementation is not a subtype of [Middleware],
    /// this field can be ignored.
    ///
    /// @return the [Priority]
    Priority priority() default Priority.NORMAL;

    /// Gets the annotation the [Validator] should be mapped to. If this class is not a subtype of [Validator],
    /// this field can be ignored.
    ///
    /// @return the annotation the [Validator][Validator]
    /// should be mapped to
    Class<? extends Annotation> annotation() default Constraint.class;

}
