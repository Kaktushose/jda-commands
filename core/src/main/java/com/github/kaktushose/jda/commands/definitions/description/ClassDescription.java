package com.github.kaktushose.jda.commands.definitions.description;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

/// A [Description] that describes a class.
///
/// @param clazz       the [Class] this [Description] describes.
/// @param name        the full name including packages of the class
/// @param packageDescription the [PackageDescription] representing the package returned by [Class#getPackage()]
/// @param annotations a [Collection] of all [Annotation]s this class is annotated with
/// @param methods     a [Collection] of all the declared [`methods`][MethodDescription] of this class (see [Class#getDeclaredMethods()])
public record ClassDescription(
        @NotNull Class<?> clazz,
        @NotNull String name,
        @NotNull PackageDescription packageDescription,
        @NotNull Collection<Annotation> annotations,
        @NotNull Collection<MethodDescription> methods
) implements Description {

    public ClassDescription(@NotNull Class<?> clazz, @NotNull String name, @NotNull PackageDescription packageDescription, Collection<Annotation> annotations, @NotNull Collection<MethodDescription> methods) {
        this.clazz = clazz;
        this.name = name;
        this.packageDescription = packageDescription;
        this.annotations = Collections.unmodifiableCollection(annotations);
        this.methods = Collections.unmodifiableCollection(methods);
    }

    @Override
    public String toString() {
        return "ClassDescription(%s)".formatted(name);
    }
}
