package com.github.kaktushose.jda.commands.definitions.description;

import java.lang.annotation.Annotation;
import java.util.Collection;

public record ParameterDescription(
        Class<?> type,
        String name,
        Collection<Annotation> annotations
) implements Description {}
