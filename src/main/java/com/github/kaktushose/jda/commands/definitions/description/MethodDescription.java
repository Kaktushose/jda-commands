package com.github.kaktushose.jda.commands.definitions.description;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.SequencedCollection;
import java.util.function.BiFunction;

public record MethodDescription(
        Class<?> returnType,
        String name,
        Collection<ParameterDescription> parameters,
        Collection<Annotation> annotations,
        BiFunction<Object, SequencedCollection<Object>, Object> invoker
) {
    public Object invoke(Object instance, SequencedCollection<Object> arguments) {
        return invoker.apply(instance, arguments);
    }
}
