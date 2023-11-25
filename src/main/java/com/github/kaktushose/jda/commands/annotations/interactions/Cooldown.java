package com.github.kaktushose.jda.commands.annotations.interactions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Commands annotated with Cooldown will have a per-user cooldown.
 *
 * @see com.github.kaktushose.jda.commands.dispatching.middleware.impl.CooldownMiddleware CooldownMiddleware
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cooldown {

    /**
     * Returns the delay of the cooldown.
     *
     * @return the delay of the cooldown
     */
    long value();

    /**
     * Returns the {@link TimeUnit} of the specified delay.
     *
     * @return the {@link TimeUnit} of the specified delay
     */
    TimeUnit timeUnit();

}
