package com.github.kaktushose.jda.commands.definitions.description;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Optional;
import java.util.SequencedCollection;

public record MethodDescription(
        Class<?> returnType,
        String name,
        Collection<ParameterDescription> parameters,
        Collection<Annotation> annotations,
        Invoker invoker
) {
    public Object invoke(Object instance, SequencedCollection<Object> arguments) throws IllegalAccessException, InvocationTargetException {
        return invoker.invoke(instance, arguments);
    }

    public <T extends Annotation> Optional<T> annotation(Class<T> type) {
        return annotations.stream().filter(type::isInstance).map(type::cast).findFirst();
    }

}
