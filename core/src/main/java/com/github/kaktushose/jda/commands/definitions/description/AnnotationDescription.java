package com.github.kaktushose.jda.commands.definitions.description;

import java.lang.annotation.Annotation;
import java.util.Collection;

/// A [Description] that describes an annotation
///
/// @param <T>         the annotations type
/// @param value       the annotations value (instance)
/// @param annotations the annotations interfaces' own annotations
/// @implSpec for the goals of JDA-Commands is sufficient that the [#annotations()] value is only one layer deep
public record AnnotationDescription<T extends Annotation>(
        T value,
        Collection<AnnotationDescription<?>> annotations
) implements Description {

    /// @return the annotations type
    /// @see Annotation#annotationType()
    @SuppressWarnings("unchecked")
    public Class<T> type() {
        return (Class<T>) value.annotationType();
    }
}
