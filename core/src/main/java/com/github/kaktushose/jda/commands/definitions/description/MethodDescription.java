package com.github.kaktushose.jda.commands.definitions.description;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodType;
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
        Class<?> declaringClass,
        Class<?> returnType,
        String name,
        SequencedCollection<ParameterDescription> parameters,
        Collection<AnnotationDescription<?>> annotations,
        Invoker invoker
) implements Description {
    public MethodDescription(Class<?> declaringClass, Class<?> returnType, String name, SequencedCollection<ParameterDescription> parameters, Collection<AnnotationDescription<?>> annotations, Invoker invoker) {
        this.declaringClass = declaringClass;
        this.returnType = returnType;
        this.name = name;
        this.parameters = Collections.unmodifiableSequencedCollection(parameters);
        this.annotations = Collections.unmodifiableCollection(annotations);
        this.invoker = invoker;
    }

    /// @return the return type and parameters of this method as a [MethodType]
    public MethodType toMethodType() {
        return MethodType.methodType(returnType, parameters.stream().map(ParameterDescription::type).toArray(Class[]::new));
    }

    @Override
    public String toString() {
        return "MethodDescription(%s)".formatted(name);
    }
}
