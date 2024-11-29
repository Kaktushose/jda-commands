package com.github.kaktushose.jda.commands.reflect.interactions.components.menus;

import com.github.kaktushose.jda.commands.annotations.interactions.DynamicOptionResolver;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DynamicOptionResolverDefinition extends GenericInteractionDefinition {

    protected DynamicOptionResolverDefinition(Method method) {
        super(method, new HashSet<>());
    }

    public static Optional<DynamicOptionResolverDefinition> build(@NotNull Method method) {
        if (!method.isAnnotationPresent(DynamicOptionResolver.class) || !method.getDeclaringClass().isAnnotationPresent(Interaction.class)) {
            return Optional.empty();
        }

        if (method.getParameters().length != 0) {
            log.error("An error has occurred! Skipping select option provider {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException(String.format("%s must have zero parameters!", DynamicOptionResolver.class.getSimpleName())));
            return Optional.empty();
        }

        if (!Set.class.isAssignableFrom(method.getReturnType())) {
            log.error("An error has occurred! Skipping select option provider {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException("Return type must be of type 'Set<SelectOption>'"));
            return Optional.empty();
        }
        return Optional.of(new DynamicOptionResolverDefinition(method));
    }
}
