package com.github.kaktushose.jda.commands.guice;

import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;
import jakarta.inject.Scope;

import java.lang.annotation.*;

/// Indicates that the annotated class is a custom implementation that should replace the default implementation.
///
/// A class annotated with [Implementation] will be automatically searched for with help of the [ClassFinder]s
/// and instantiated by guice. Following types are candidates for automatic registration.
///
/// - [Middleware]
/// - [Validator]
/// - [TypeAdapter]
/// - [PermissionsProvider]
/// - [GuildScopeProvider]
/// - [ErrorMessageFactory]
/// - [Descriptor]
///
/// ### Example
/// ```java
/// @Implementation(priority = Priority.NORMAL)
/// public class CustomMiddleware implements Middleware {
///     private static final Logger log = LoggerFactory.getLogger(FirstMiddleware.class);
///
///     @Override
///     public void accept(InvocationContext<?> context) {
///         log.info("run custom middleware");
///     }
/// }
/// ```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Scope
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


    /// Gets the [Class] to register a [TypeAdapter] with. If this implementation is not a subtype of [TypeAdapter],
    /// this field can be ignored.
    ///
    /// @return the class the [TypeAdapter] should be mapped to
    Class<?> clazz() default Object.class;



}
