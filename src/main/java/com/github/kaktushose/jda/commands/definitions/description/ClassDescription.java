package com.github.kaktushose.jda.commands.definitions.description;

import java.lang.annotation.Annotation;
import java.util.Collection;

public record ClassDescription(
        Class<?> clazz,
        String name,
        Collection<Annotation> annotations,
        Collection<MethodDescription> methods
) implements Description {}
