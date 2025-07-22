package com.github.kaktushose.jda.commands.definitions.description;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/// A [Description] that describes a [Package].
///
/// @param name the package's name
/// @param annotations the package's `package-info.java` file's annotations
public record PackageDescription(
        @NotNull String name,
        @NotNull Collection<AnnotationDescription<?>> annotations
) implements Description {}
