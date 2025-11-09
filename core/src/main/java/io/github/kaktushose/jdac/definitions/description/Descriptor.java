package io.github.kaktushose.jdac.definitions.description;

import io.github.kaktushose.jdac.definitions.description.reflective.ReflectiveDescriptor;
import io.github.kaktushose.jdac.extension.Implementation;

/// A [Descriptor] takes a [Class] as input and transforms it into a [ClassDescription].
@FunctionalInterface
public non-sealed interface Descriptor extends Implementation.ExtensionProvidable {

    /// the default [Descriptor], which builds [ClassDescription] using [java.lang.reflect]
    Descriptor REFLECTIVE = new ReflectiveDescriptor();

    /// Transforms the given [Class] into a [ClassDescription].
    ///
    /// @param clazz the [Class] to transform
    /// @return the [ClassDescription] built from the given [Class]
    ClassDescription describe(Class<?> clazz);
}
