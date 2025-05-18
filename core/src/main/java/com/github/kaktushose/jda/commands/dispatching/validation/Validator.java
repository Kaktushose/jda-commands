package com.github.kaktushose.jda.commands.dispatching.validation;

import com.github.kaktushose.jda.commands.JDACBuilder;
import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import org.jetbrains.annotations.NotNull;

/// Validators check if a command option fulfills the given constraint.
///
/// Register them at the [JDACBuilder#validator(Class, Validator)()].
///
/// ### Example
/// ```java
/// @Target(ElementType.PARAMETER)
/// @Retention(RetentionPolicy.RUNTIME)
/// @Constraint(String.class)
/// public @interface MaxString {
///     int value();
///     String message() default "The given String is too long";
/// }
///
/// public class MaxStringLengthValidator implements Validator {
///
///     @Override
///     public boolean apply(Object argument, Object annotation, InvocationContext<? context) {
///         MaxString maxString = (MaxString) annotation;
///         return String.valueOf(argument).length() < maxString.value();
///     }
/// }
/// ```
/// @see Constraint
@FunctionalInterface
public interface Validator {

    /// Validates an argument.
    ///
    /// @param argument   the argument to validate
    /// @param annotation the corresponding annotation
    /// @param context    the corresponding [InvocationContext]
    /// @return `true` if the argument passes the constraints
    boolean apply(@NotNull Object argument, @NotNull Object annotation, @NotNull InvocationContext<?> context);

}
