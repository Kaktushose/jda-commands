package io.github.kaktushose.jdac.annotations.interactions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/// Commands annotated with Cooldown will have a per-user cooldown.
///
/// @see io.github.kaktushose.jdac.dispatching.middleware.impl.CooldownMiddleware CooldownMiddleware
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cooldown {

    /// Returns the delay of the cooldown.
    ///
    /// @return the delay of the cooldown
    long value();

    /// Returns the [TimeUnit] of the specified delay.
    ///
    /// @return the [TimeUnit] of the specified delay
    TimeUnit timeUnit();

}
