package com.github.kaktushose.jda.commands.definitions.description;

import java.lang.annotation.Annotation;
import java.util.Collection;

public record AnnotationDescription<T extends Annotation>(
        T value,
        Collection<AnnotationDescription<?>> annotations
) implements Description {

    @SuppressWarnings("unchecked")
    public Class<T> type() {
        return (Class<T>) value.annotationType();
    }
}
