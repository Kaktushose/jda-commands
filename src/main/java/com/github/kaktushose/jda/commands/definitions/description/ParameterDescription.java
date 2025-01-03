package com.github.kaktushose.jda.commands.definitions.description;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.Collection;

/// A [Description] that describes a method.
///
/// @param type the [Class] representing the type of this parameter
/// @param name the name of the parameter
/// @param annotations a [Collection] of all [Annotation]s this parameter is annotated with
public record ParameterDescription(
        @NotNull Class<?> type,
        @NotNull String name,
        @NotNull Collection<Annotation> annotations
) implements Description {}
