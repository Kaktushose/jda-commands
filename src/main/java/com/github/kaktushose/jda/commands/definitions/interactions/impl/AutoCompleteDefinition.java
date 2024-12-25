package com.github.kaktushose.jda.commands.definitions.interactions.impl;

import com.github.kaktushose.jda.commands.definitions.interactions.Interaction;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.SequencedCollection;
import java.util.Set;

public record AutoCompleteDefinition(
        @NotNull Method method,
        @NotNull SequencedCollection<Class<?>> methodSignature,
        @NotNull Set<String> commands
) implements Interaction {

    @Override
    public String displayName() {
        return "%s.%s".formatted(method.getDeclaringClass().getName(), method.getName());
    }
}
