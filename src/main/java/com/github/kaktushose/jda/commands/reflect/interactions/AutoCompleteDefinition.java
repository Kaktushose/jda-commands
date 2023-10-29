package com.github.kaktushose.jda.commands.reflect.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.AutoComplete;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.interactions.autocomplete.AutoCompleteEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Representation of an AutoComplete.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see AutoComplete
 * @since 4.0.0
 */
public class AutoCompleteDefinition extends GenericInteraction {

    private Set<String> commands;

    protected AutoCompleteDefinition(Method method, Set<String> commands) {
        super(method);
        this.commands = commands;
    }

    /**
     * Builds a new AutoCompleteDefinition.
     *
     * @param method the {@link Method} of the AutoComplete
     * @return an {@link Optional} holding the AutoCompleteDefinition
     */
    public static Optional<AutoCompleteDefinition> build(@NotNull Method method, List<String> autoCompletes) {
        if (!method.isAnnotationPresent(AutoComplete.class) || !method.getDeclaringClass().isAnnotationPresent(Interaction.class)) {
            return Optional.empty();
        }

        if (method.getParameters().length != 1) {
            log.error("An error has occurred! Skipping auto complete {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException("Invalid amount of parameters!"));
            return Optional.empty();
        }

        if (!AutoCompleteEvent.class.isAssignableFrom(method.getParameters()[0].getType())) {
            log.error("An error has occurred! Skipping auto complete {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException(String.format("First parameter must be of type %s", AutoCompleteEvent.class.getSimpleName())));
            return Optional.empty();
        }

        AutoComplete autoComplete = method.getAnnotation(AutoComplete.class);
        Set<String> values = Set.of(autoComplete.value());

        if (autoCompletes.stream().anyMatch(values::contains)) {
            log.error("An error has occurred! Skipping auto complete {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalStateException(String.format("There is already an auto complete handler registered " +
                            "for at least one of the following commands: %s", values)));
            return Optional.empty();
        }

        return Optional.of(new AutoCompleteDefinition(method, values));
    }

    /**
     * Set the command names this AutoComplete can handle
     *
     * @param commands a set of command names
     */
    public void setCommandNames(Set<String> commands) {
        this.commands = commands;
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
                ", id='" + id + "'" +
                ", method=" + method +
                '}';
    }
}
