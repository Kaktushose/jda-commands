package com.github.kaktushose.jda.commands.guice.annotation;


import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import jakarta.inject.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Indicates that the annotated class is a custom implementation that should replace the default implementation.
///
/// A class annotated with [Middlewares] will be automatically searched for with help of the [ClassFinder]s
/// and instantiated as a [Middleware] by guice.
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
public @interface Middlewares {

    /// Gets the [Priority] to register the [Middleware] with. If this implementation is not a subtype of [Middleware],
    /// this field can be ignored.
    ///
    /// @return the [Priority]
    Priority priority() default Priority.NORMAL;

}
