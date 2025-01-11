package com.github.kaktushose.jda.commands.definitions.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.AutoComplete;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.internal.Helpers;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/// Representation of an auto complete handler.
///
/// @param clazzDescription  the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription the [MethodDescription] of the method this definition is bound to
/// @param commands          the commands this autocomplete handler can handle
public record AutoCompleteDefinition(@NotNull ClassDescription clazzDescription,
                                     @NotNull MethodDescription methodDescription,
                                     @NotNull Set<String> commands)
        implements InteractionDefinition {

    /// Builds a new [AutoCompleteDefinition] from the given class and method description.
    ///
    /// @param clazz  the corresponding [ClassDescription]
    /// @param method the corresponding [MethodDescription]
    /// @return an [Optional] holding the [AutoCompleteDefinition]
    @NotNull
    public static Optional<AutoCompleteDefinition> build(@NotNull ClassDescription clazz, @NotNull MethodDescription method) {
        if (Helpers.checkSignature(method, List.of(AutoCompleteEvent.class))) {
            return Optional.empty();
        }
        return method.annotation(AutoComplete.class).map(complete ->
                new AutoCompleteDefinition(clazz, method, Arrays.stream(complete.value()).collect(Collectors.toSet()))
        );

    }

    @NotNull
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
