package com.github.kaktushose.jda.commands.reflect.interactions.components.menus;

import com.github.kaktushose.jda.commands.annotations.interactions.DynamicOptionResolver;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.reflect.interactions.InteractionRuntimeExecutable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Optional;

/**
 * Representation of a {@link DynamicOptionResolver}. This class is only used internally.
 *
 * @see com.github.kaktushose.jda.commands.annotations.interactions.DynamicOptions DynamicOptions
 * @since 4.0.0
 */
public class DynamicOptionResolverDefinition extends InteractionRuntimeExecutable {

    private static final Logger log = LoggerFactory.getLogger(DynamicOptionResolverDefinition.class);

    protected DynamicOptionResolverDefinition(Method method) {
        super(method);
    }

    /**
     * Constructs a new DynamicOptionResolverDefinition.
     *
     * @param method the {@link Method} of the DynamicOptionResolver
     * @return an Optional holding the DynamicOptionResolverDefinition or an empty Optional if the building failed
     */
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

        if (!Collection.class.isAssignableFrom(method.getReturnType())) {
            log.error("An error has occurred! Skipping select option provider {}.{}:",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName(),
                    new IllegalArgumentException("Return type must be of type 'Collection<SelectOption>'"));
            return Optional.empty();
        }
        return Optional.of(new DynamicOptionResolverDefinition(method));
    }
}
