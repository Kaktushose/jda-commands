package com.github.kaktushose.jda.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Fields annotated with Inject will be assigned a value that is provided by a [Produces] method.
/// If no Producer for the field type is available then the field will be assigned `null`. Please note, that each
/// field type can only have one producer.
///
/// @see Produces
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
}
