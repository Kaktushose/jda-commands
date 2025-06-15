package com.github.kaktushose.jda.commands.definitions.description;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

/// A [Description] that describes a parameter.
///
/// @param type        the [Class] representing the type of this parameter
/// @param name        the name of the parameter
/// @param annotations a [Collection] of all [Annotation]s this parameter is annotated with
public record ParameterDescription(
        @NotNull Class<?> type,
        @NotNull String name,
        @NotNull Collection<AnnotationDescription<?>> annotations
) implements Description {
    public ParameterDescription(@NotNull Class<?> type, @NotNull String name, @NotNull Collection<AnnotationDescription<?>> annotations) {
        this.type = type;
        this.name = name;
        this.annotations = Collections.unmodifiableCollection(annotations);
    }

    @Override
    public String toString() {
        return "ParameterDescription{" +
                "type=" + type +
                ", name='" + name + '\'' +
                '}';
    }
}
