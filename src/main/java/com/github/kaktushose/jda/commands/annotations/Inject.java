package com.github.kaktushose.jda.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields annotated with Inject will be assigned a value that is provided by a {@link Produces} method.
 * If no Producer for the field type is available then the field will be assigned {@code null}. Please note, that each
 * field type can only have one producer.
 *
 * @see Produces
 * @since 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
}
