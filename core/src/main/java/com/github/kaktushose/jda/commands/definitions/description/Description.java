package com.github.kaktushose.jda.commands.definitions.description;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.Collection;
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
    @NotNull
    Collection<AnnotationDescription<?>> annotations();

    /// Gets this element's [Annotation] for the specified type if such an annotation is present
    ///
    /// @param type the type of the annotation to get
    /// @return an [Optional] holding the [Annotation] if present at this element or else an empty [Optional]
    @SuppressWarnings("unchecked")
    @NotNull
    default <T extends Annotation> Optional<T> annotation(@NotNull Class<T> type) {
        return annotations().stream()
                .filter(ann -> ann.type().equals(type))
                .map(ann -> ((T) ann.value()))
                .findFirst();
    }
}
