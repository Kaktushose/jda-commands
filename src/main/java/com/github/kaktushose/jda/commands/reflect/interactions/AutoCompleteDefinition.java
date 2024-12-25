package com.github.kaktushose.jda.commands.reflect.interactions;

import com.github.kaktushose.jda.commands.internal.Helpers;
import com.github.kaktushose.jda.commands.annotations.interactions.AutoComplete;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.AutoCompleteEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Representation of an AutoComplete.
 *
 * @see AutoComplete
 * @since 4.0.0
 */
public final class AutoCompleteDefinition extends GenericInteractionDefinition {

    private final Set<String> commands;

    private AutoCompleteDefinition(Method method, Set<String> commands) {
        super(method, new HashSet<>());
        this.commands = commands;
    }

    /**
     * Builds a new AutoCompleteDefinition.
     *
     * @param method the {@link Method} of the AutoComplete
     * @return an {@link Optional} holding the AutoCompleteDefinition
     */
    public static Optional<AutoCompleteDefinition> build(@NotNull Method method) {
        if (!method.isAnnotationPresent(AutoComplete.class) || !method.getDeclaringClass().isAnnotationPresent(Interaction.class)) {
            return Optional.empty();
        }

        if (Helpers.isIncorrectParameterAmount(method, 1)) {
            return Optional.empty();
        }

        if (Helpers.isIncorrectParameterType(method, 0, AutoCompleteEvent.class)) {
            return Optional.empty();
        }

        AutoComplete autoComplete = method.getAnnotation(AutoComplete.class);
        Set<String> values = Set.of(autoComplete.value());

        return Optional.of(new AutoCompleteDefinition(method, values));
    }

    /**
     * Gets a set of names of all the commands this AutoComplete can handle
     *
     * @return a set of command names
     */
    public Set<String> getCommandNames() {
        return commands;
    }

    @Override
    public String toString() {
        return "AutoCompleteDefinition{" +
                "commands=" + commands +
                ", id='" + definitionId + "'" +
                ", method=" + method +
                '}';
    }
}
