package com.github.kaktushose.jda.commands.reflect.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.AutoComplete;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.interactions.autocomplete.AutoCompleteEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
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

    private final Set<String> commands;

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
    public static Optional<AutoCompleteDefinition> build(@NotNull Method method) {
        if (!method.isAnnotationPresent(AutoComplete.class) || !method.getDeclaringClass().isAnnotationPresent(Interaction.class)) {
            return Optional.empty();
        }

        if (method.getParameters().length != 2) {
            log.error("An error has occurred! Skipping auto complete {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException("Invalid amount of parameters!"));
            return Optional.empty();
        }

        if (!AutoCompleteEvent.class.isAssignableFrom(method.getParameters()[0].getType()) &&
                !AutoCompleteQuery.class.isAssignableFrom(method.getParameters()[1].getType())) {
            log.error("An error has occurred! Skipping auto complete {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException(String.format("First parameter must be of type %s, second parameter of type %s!",
                            AutoCompleteEvent.class.getSimpleName(),
                            AutoCompleteQuery.class.getSimpleName()
                    )));
            return Optional.empty();
        }

        AutoComplete autoComplete = method.getAnnotation(AutoComplete.class);

        return Optional.of(new AutoCompleteDefinition(method, Set.of(autoComplete.value())));
    }

    /**
     * Gets a set of names of all the commands this AutoComplete can handle
     *
     * @return a set of command names
     */
    public Set<String> getCommandNames() {
        return commands;
    }
}
