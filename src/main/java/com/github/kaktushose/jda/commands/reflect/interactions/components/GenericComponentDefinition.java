package com.github.kaktushose.jda.commands.reflect.interactions.components;

import com.github.kaktushose.jda.commands.reflect.interactions.CustomId;
import com.github.kaktushose.jda.commands.reflect.interactions.EphemeralInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.ReplyConfig;
import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.GenericSelectMenuDefinition;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Marker class for component definitions.
 *
 * @see ButtonDefinition
 * @see com.github.kaktushose.jda.commands.reflect.interactions.components.menus.GenericSelectMenuDefinition GenericSelectMenuDefinition
 * @since 4.0.0
 */
public abstract sealed class GenericComponentDefinition extends EphemeralInteractionDefinition implements CustomId
        permits ButtonDefinition, GenericSelectMenuDefinition {

    protected GenericComponentDefinition(Method method, Set<String> permissions, ReplyConfig replyConfig) {
        super(method, permissions, replyConfig);
    }

    @Override
    public String scopedCustomId(String runtimeId) {
        return "%s.%s.%s".formatted(PREFIX, runtimeId, definitionId);
    }

    @Override
    public String staticCustomId() {
        return "%s.static.%s".formatted(PREFIX, definitionId);
    }
}
