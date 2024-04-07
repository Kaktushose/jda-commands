package com.github.kaktushose.jda.commands.reflect.interactions.components;

import com.github.kaktushose.jda.commands.reflect.interactions.CustomId;
import com.github.kaktushose.jda.commands.reflect.interactions.EphemeralInteractionDefinition;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Marker class for component definitions.
 *
 * @see ButtonDefinition
 * @see com.github.kaktushose.jda.commands.reflect.interactions.components.menus.GenericSelectMenuDefinition GenericSelectMenuDefinition
 * @since 4.0.0
 */
public abstract class GenericComponentDefinition extends EphemeralInteractionDefinition implements CustomId {

    protected GenericComponentDefinition(Method method, Set<String> permissions, boolean ephemeral) {
        super(method, permissions, ephemeral);
    }

    @Override
    public String createCustomId(String runtimeId) {
        return String.format("%s.%s%s.%s",
                PREFIX,
                method.getDeclaringClass().getSimpleName(),
                method.getName(),
                runtimeId
        );
    }
}
