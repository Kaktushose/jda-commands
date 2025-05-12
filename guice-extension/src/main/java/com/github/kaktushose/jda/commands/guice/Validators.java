package com.github.kaktushose.jda.commands.guice;

import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.validation.Validator;
import jakarta.inject.Scope;

import java.lang.annotation.*;

/// Indicates that the annotated class is a custom implementation that should replace the default implementation.
///
/// A class annotated with [TypeAdapters] will be automatically searched for with help of the [ClassFinder]s
/// and instantiated as a [TypeAdapter] by guice.
///
/// ### Example
/// ```java
/// @Target(ElementType.PARAMETER)
/// @Retention(RetentionPolicy.RUNTIME)
/// @Constraint(String.class)
/// publc @interface MaxString {
///     int value();
///     String message() default "The given String is too long";
/// }
///
/// @Validators(annotation = MaxString.class)
/// public class MaxStringLengthValidator implements Validator {
///
///     @Override
///    public boolean apply(Object argument, Object annotation, InvocationContext<? context) {
///         MaxString maxString = (MaxString) annotation;
///         return String.valueOf(argument).length() < maxString.value();
///     }
/// }
/// ```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Scope
public @interface Validators {

    /// Gets the annotation the [Validator] should be mapped to. If this class is not a subtype of [Validator],
    /// this field can be ignored.
    ///
    /// @return the annotation the [Validator] should be mapped to
    Class<? extends Annotation> annotation() default Constraint.class;

}
