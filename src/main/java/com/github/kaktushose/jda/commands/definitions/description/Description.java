package com.github.kaktushose.jda.commands.definitions.description;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Optional;

public sealed interface Description permits ClassDescription, MethodDescription, ParameterDescription {
    Collection<Annotation> annotations();

    default <T extends Annotation> Optional<T> annotation(Class<T> type) {
        return annotations().stream().filter(type::isInstance).map(type::cast).findFirst();
    }
}
