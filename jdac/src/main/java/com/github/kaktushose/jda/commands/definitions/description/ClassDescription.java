package com.github.kaktushose.jda.commands.definitions.description;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

/// A [Description] that describes a class.
///
/// @param clazz       the [Class] this [Description] describes.
/// @param name        the full name including packages of the class
/// @param annotations a [Collection] of all [Annotation]s this class is annotated with
/// @param methods     a [Collection] of all the public [`methods`][MethodDescription] of this class
public record ClassDescription(
        @NotNull Class<?> clazz,
        @NotNull String name,
        @NotNull Collection<Annotation> annotations,
        @NotNull Collection<MethodDescription> methods
) implements Description {
    public ClassDescription(@NotNull Class<?> clazz, @NotNull String name, @NotNull Collection<Annotation> annotations, @NotNull Collection<MethodDescription> methods) {
        this.clazz = clazz;
        this.name = name;
        this.annotations = Collections.unmodifiableCollection(annotations);
        this.methods = Collections.unmodifiableCollection(methods);
    }
}
