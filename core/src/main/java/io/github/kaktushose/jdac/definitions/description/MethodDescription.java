package io.github.kaktushose.jdac.definitions.description;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodType;
import java.util.*;

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

    /// Gets the parameter matching the given name if any.
    ///
    /// Please note that if `-parameters` isn't present on the 'javac' command, this is just `arg0`, `arg1` etc.
    /// if using [Descriptor#REFLECTIVE].
    ///
    /// @param name the parameters name
    /// @return the matching [ParameterDescription] or [Optional#empty()]
    public Optional<ParameterDescription> findParameter(String name) {
        return parameters.stream().filter(desc -> desc.name().equals(name)).findFirst();
    }

    /// Gets the parameter matching the given name. Throws if no matching parameter is found.
    ///
    /// Please note that if `-parameters` isn't present on the 'javac' command, this is just `arg0`, `arg1` etc.
    /// if using [Descriptor#REFLECTIVE].
    ///
    /// @param name the parameters name
    /// @return the matching [ParameterDescription]
    ///
    /// @throws NoSuchElementException if no element was found
    /// @see Optional#orElseThrow()
    public ParameterDescription parameter(String name) {
        return findParameter(name).orElseThrow();
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
