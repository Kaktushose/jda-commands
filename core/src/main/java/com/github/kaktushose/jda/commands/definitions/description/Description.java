package com.github.kaktushose.jda.commands.definitions.description;

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
public sealed interface Description permits ClassDescription, MethodDescription, ParameterDescription {

    /// a possibly-empty [Collection] of all [Annotation]s this element is annotated with.
    
    Collection<Annotation> annotations();

    /// Gets this element's [Annotation] for the specified type if such an annotation is present
    ///
    /// @param type the type of the annotation to get
    /// @return an [Optional] holding the [Annotation] if present at this element or else an empty [Optional]
    
    default <T extends Annotation> Optional<T> annotation(Class<T> type) {
        return annotations().stream().filter(type::isInstance).map(type::cast).findFirst();
    }
}
