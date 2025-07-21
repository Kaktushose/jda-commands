package com.github.kaktushose.jda.commands.definitions.description;

import org.jspecify.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;

/// A [Description] that describes a parameter.
///
/// @param type        the [Class] representing the declaredType of this parameter
/// @param name        the name of the parameter
/// @param typeArguments the generic type arguments of the type ([ParameterizedType#getActualTypeArguments()]).
///        They only represent the first layer and are all raw types represented as [Class] instances or null if wildcard/no class.
/// @param annotations a [Collection] of all [AnnotationDescription]s this parameter is annotated with
public record ParameterDescription(
        Class<?> type,
        @Nullable Class<?> [] typeArguments,
        String name,
        Collection<AnnotationDescription<?>> annotations
) implements Description {
    public ParameterDescription(Class<?> type, Class<?>[] typeArguments, String name, Collection<AnnotationDescription<?>> annotations) {
        this.type = type;
        this.name = name;
        this.typeArguments = typeArguments;
        this.annotations = Collections.unmodifiableCollection(annotations);
    }

    @Override
    public String toString() {
        return "ParameterDescription{" +
                "declaredType=" + type +
                ", name='" + name + '\'' +
                '}';
    }
}
