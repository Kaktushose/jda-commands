package com.github.kaktushose.jda.commands.definitions.interactions.impl;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.features.Invokeable;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.AutoCompleteEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.SequencedCollection;
import java.util.Set;

public record AutoCompleteDefinition(
        @NotNull ClassDescription clazz,
        @NotNull MethodDescription method,
        @NotNull Set<String> commands
) implements Definition, Invokeable {

    @Override
    public String displayName() {
        return "%s.%s".formatted(clazz().name(), method.name());
    }

    @NotNull
    @Override
    public SequencedCollection<Class<?>> methodSignature() {
        return List.of(AutoCompleteEvent.class);
    }
}
