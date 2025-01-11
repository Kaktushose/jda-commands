package com.github.kaktushose.jda.commands.definitions.description;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.SequencedCollection;

/// A [Description] that describes a method.
///
/// @param declaringClass the declaring [Class] of this method
/// @param returnType     the [Class] this method returns
/// @param name           the name of the method
/// @param parameters     a [SequencedCollection] of the [ParameterDescription]s of this method
/// @param annotations    a [Collection] of all [Annotation]s this method is annotated with
/// @param invoker        the corresponding [Invoker], used to invoke this method
public record MethodDescription(
        @NotNull Class<?> declaringClass,
        @NotNull Class<?> returnType,
        @NotNull String name,
        @NotNull SequencedCollection<ParameterDescription> parameters,
        @NotNull Collection<Annotation> annotations,
        @NotNull Invoker invoker
) implements Description {
    public MethodDescription(@NotNull Class<?> declaringClass, @NotNull Class<?> returnType, @NotNull String name, @NotNull SequencedCollection<ParameterDescription> parameters, @NotNull Collection<Annotation> annotations, @NotNull Invoker invoker) {
        this.declaringClass = declaringClass;
        this.returnType = returnType;
        this.name = name;
        this.parameters = Collections.unmodifiableSequencedCollection(parameters);
        this.annotations = Collections.unmodifiableCollection(annotations);
        this.invoker = invoker;
    }
}
