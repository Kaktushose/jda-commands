package io.github.kaktushose.jdac.annotations.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// The annotated element must be a number whose value must be greater or equal to the specified minimum.
///
/// @see Constraint
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint({Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class})
public @interface Min {

    /// Returns the value the element must be greater or equal to.
    ///
    /// @return Returns the value the element must be greater or equal to
    long value();
}
