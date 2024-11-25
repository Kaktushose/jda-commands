package com.github.kaktushose.jda.commands.reflect.interactions.components.menus;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.SelectOptionProvider;
import com.github.kaktushose.jda.commands.dispatching.interactions.components.SelectOptionEvent;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;

public class SelectOptionProviderDefinition extends GenericInteractionDefinition {

    protected SelectOptionProviderDefinition(Method method) {
        super(method, new HashSet<>());
    }

    public static Optional<SelectOptionProviderDefinition> build(@NotNull Method method) {
        if (!method.isAnnotationPresent(SelectOptionProvider.class) || !method.getDeclaringClass().isAnnotationPresent(Interaction.class)) {
            return Optional.empty();
        }

        if (method.getParameters().length != 1) {
            log.error("An error has occurred! Skipping select option provider {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException("Invalid amount of parameters!"));
            return Optional.empty();
        }

        if (!SelectOptionEvent.class.isAssignableFrom(method.getParameters()[0].getType())) {
            log.error("An error has occurred! Skipping select option provider {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException(String.format("First parameter must be of type %s", SelectOptionEvent.class.getSimpleName())));
            return Optional.empty();
        }
        return Optional.of(new SelectOptionProviderDefinition(method));
    }
}
