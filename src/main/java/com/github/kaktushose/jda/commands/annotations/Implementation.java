package com.github.kaktushose.jda.commands.annotations;

import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.dispatching.ImplementationRegistry;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;

import java.lang.annotation.*;

/// Indicates that the annotated class is a custom implementation that should replace the default implementation.
///
/// @see ImplementationRegistry
/// @see Middleware
/// @see Validator
/// @see TypeAdapter
/// @see PermissionsProvider
/// @see GuildScopeProvider
/// @see ErrorMessageFactory
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
    /// @return the annotation the [Validator] should be mapped to
    Class<? extends Annotation> annotation() default Constraint.class;

}
