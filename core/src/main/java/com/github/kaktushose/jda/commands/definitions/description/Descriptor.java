package com.github.kaktushose.jda.commands.definitions.description;

import com.github.kaktushose.jda.commands.definitions.description.reflective.ReflectiveDescriptor;
import com.github.kaktushose.jda.commands.extension.Implementation;

/// A [Descriptor] takes a [Class] as input and transforms it into a [ClassDescription].
@FunctionalInterface
public non-sealed interface Descriptor extends Implementation.ExtensionImplementable {

    /// the default [Descriptor], which builds [ClassDescription] using [java.lang.reflect]
    Descriptor REFLECTIVE = new ReflectiveDescriptor();

    /// Transforms the given [Class] into a [ClassDescription].
    ///
    /// @param clazz the [Class] to transform
    /// @return the [ClassDescription] built from the given [Class]
    ClassDescription describe(Class<?> clazz);
}
