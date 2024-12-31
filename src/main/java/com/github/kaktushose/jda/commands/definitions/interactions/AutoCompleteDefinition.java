package com.github.kaktushose.jda.commands.definitions.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.AutoComplete;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.internal.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public record AutoCompleteDefinition(@NotNull ClassDescription clazzDescription, @NotNull MethodDescription methodDescription,
                                     @NotNull Set<String> commands)
        implements InteractionDefinition {

    @Nullable
    public static AutoCompleteDefinition build(@NotNull ClassDescription clazz, @NotNull MethodDescription method) {
        if (Helpers.checkSignature(method, List.of(AutoCompleteEvent.class))) {
            return null;
        }
        return method.annotation(AutoComplete.class).map(complete ->
                new AutoCompleteDefinition(clazz, method, Arrays.stream(complete.value()).collect(Collectors.toSet()))
        ).orElse(null);

    }

    @Override
    public String displayName() {
        return "%s.%s".formatted(methodDescription.declaringClass().getName(), methodDescription.name());
    }

    @NotNull
    @Override
    public Collection<String> permissions() {
        return Collections.emptyList();
    }

}
