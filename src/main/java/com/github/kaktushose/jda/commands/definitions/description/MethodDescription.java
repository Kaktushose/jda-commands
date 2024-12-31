package com.github.kaktushose.jda.commands.definitions.description;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.SequencedCollection;

public record MethodDescription(
        Class<?> declaringClass,
        Class<?> returnType,
        String name,
        SequencedCollection<ParameterDescription> parameters,
        Collection<Annotation> annotations,
        Invoker invoker
) implements Description {
    public Object invoke(Object instance, SequencedCollection<Object> arguments) throws IllegalAccessException, InvocationTargetException {
        return invoker.invoke(instance, arguments);
    }
}
