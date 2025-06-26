package com.github.kaktushose.jda.commands.annotations.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Indicates that an annotation type can be used for parameter validation. When implementing custom validators, the
/// annotation type must be annotated with this annotation.
///
/// @see com.github.kaktushose.jda.commands.dispatching.validation.Validator
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Constraint {

    /// Returns an array of all types this annotation can be used for.
    ///
    /// @return an array of all types this annotation can be used for
    Class<?>[] value();
}
