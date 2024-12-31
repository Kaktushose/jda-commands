package com.github.kaktushose.jda.commands.definitions.description;

import java.lang.annotation.Annotation;
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
}
