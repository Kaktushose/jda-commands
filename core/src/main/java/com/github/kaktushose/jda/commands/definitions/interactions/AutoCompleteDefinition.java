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
/// @param classDescription  the [ClassDescription] of the declaring class of the [#methodDescription()]
/// @param methodDescription the [MethodDescription] of the method this definition is bound to
/// @param rules          the rules this autocomplete handler can handle
public record AutoCompleteDefinition(@NotNull ClassDescription classDescription,
                                     @NotNull MethodDescription methodDescription,
                                     @NotNull Set<AutoCompleteRule> rules)
        implements InteractionDefinition {

    /// Representation of an auto complete rule.
    ///
    /// @param command the name of the slash command or the name of the method handling the command
    /// @param options a possibly-empty Set of the names of the options the auto complete should exclusively handle. If
    ///                                                                                                                                        empty, the auto complete will handle every option of the given command.
    public record AutoCompleteRule(@NotNull String command, @NotNull Set<String> options) {}

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
        return method.annotation(AutoComplete.class).map(autoComplete ->
                new AutoCompleteDefinition(clazz, method, Arrays.stream(autoComplete.value())
                        .map(command -> new AutoCompleteRule(command, Arrays.stream(autoComplete.options())
                                .filter(it -> !it.isBlank())
                                .collect(Collectors.toSet()))
                        ).collect(Collectors.toSet())
                ));
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
