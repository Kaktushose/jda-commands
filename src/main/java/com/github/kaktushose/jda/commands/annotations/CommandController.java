package com.github.kaktushose.jda.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes annotated with CommandController will be scanned at startup.
 *
 * <p>All methods annotated with {@link Command} will be registered as a command.
 * All fields annotated with {@link Inject} will be assigned a value. If any errors occur only the affected method or field
 * will be skipped.
 *
 * @author Kaktushose
 * @version 1.0.0
 * @see Command
 * @see Inject
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandController {
    String[] value() default "";

    boolean isActive() default true;
}
