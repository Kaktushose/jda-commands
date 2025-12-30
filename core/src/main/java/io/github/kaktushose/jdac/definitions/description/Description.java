package io.github.kaktushose.jdac.definitions.description;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

/// The common interface of all [Description] types.
///
/// A [Description] is, as the name says, a description of a class, method or parameter, similar to how
/// `java.lang.reflect` works.
///
/// @see ClassDescription
/// @see MethodDescription
/// @see ParameterDescription
public sealed interface Description permits AnnotationDescription, ClassDescription, MethodDescription, PackageDescription, ParameterDescription {

    /// a possibly-empty [Collection] of all [Annotation]s this element is annotated with.
    Collection<AnnotationDescription<?>> annotations();

    /// Gets this element's [Annotation] for the specified type if such an annotation is present
    ///
    /// @param type the type of the annotation to get
    /// @return an [Optional] holding the [Annotation] if present at this element or else an empty [Optional]
    @SuppressWarnings("unchecked")
    default <T extends Annotation> Optional<T> findAnnotation(Class<T> type) {
        return annotations().stream()
                .filter(ann -> ann.type().equals(type))
                .map(ann -> ((T) ann.value()))
                .findFirst();
    }

    /// Gets this element's [Annotation] for the specified type. Throws if no matching annotation is found.
    ///
    /// @param type the type of the annotation to get
    /// @return an [Optional] holding the [Annotation]
    ///
    /// @throws NoSuchElementException if no element was found
    /// @see Optional#orElseThrow()
    default <T extends Annotation> T annotation(Class<T> type) {
        return findAnnotation(type).orElseThrow();
    }
}
