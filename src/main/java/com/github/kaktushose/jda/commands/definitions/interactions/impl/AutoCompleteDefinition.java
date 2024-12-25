package com.github.kaktushose.jda.commands.definitions.interactions.impl;

import com.github.kaktushose.jda.commands.annotations.interactions.AutoComplete;
import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.features.Invokeable;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.internal.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record AutoCompleteDefinition(@NotNull MethodDescription method, @NotNull Set<String> commands)
        implements Definition, Invokeable {

    @Nullable
    public static AutoCompleteDefinition build(@NotNull MethodDescription method) {
        if (Helpers.checkSignature(method, List.of(AutoCompleteEvent.class))) {
            return null;
        }
        return method.annotation(AutoComplete.class).map(complete ->
                new AutoCompleteDefinition(method, Arrays.stream(complete.value()).collect(Collectors.toSet()))
        ).orElse(null);

    }

    @Override
    public String displayName() {
        return "%s.%s".formatted(method.declaringClass().getName(), method.name());
    }
}
