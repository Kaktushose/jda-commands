package io.github.kaktushose.jdac.definitions.description;

import java.util.Collection;

/// A [Description] that describes a [Package].
///
/// @param name        the packages name
/// @param annotations the packages `package-info.java` files annotations
public record PackageDescription(
        String name,
        Collection<AnnotationDescription<?>> annotations
) implements Description { }
