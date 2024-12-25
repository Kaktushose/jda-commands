package com.github.kaktushose.jda.commands.definitions.description;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Optional;

public record ClassDescription(
        Class<?> clazz,
        String name,
        Collection<Annotation> annotations,
        Collection<MethodDescription> methods
) {

    public <T extends Annotation> Optional<T> annotation(Class<T> type) {
        return annotations.stream().filter(type::isInstance).map(type::cast).findFirst();
    }

}
