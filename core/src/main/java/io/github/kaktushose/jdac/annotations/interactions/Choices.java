package io.github.kaktushose.jdac.annotations.interactions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Annotation used to add choices to parameters.
///
/// @see Command
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Choices {

    /// Returns the choices of a parameter. This value will only be used for slash commands.
    ///
    /// @return the choices of the parameter
    String[] value();

}
