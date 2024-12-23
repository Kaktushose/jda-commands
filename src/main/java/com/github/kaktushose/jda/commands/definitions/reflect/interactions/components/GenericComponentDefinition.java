package com.github.kaktushose.jda.commands.definitions.reflect.interactions.components;

import com.github.kaktushose.jda.commands.definitions.reflect.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.EphemeralInteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.ReplyConfig;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.components.menus.GenericSelectMenuDefinition;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Marker class for name definitions.
 *
 * @see ButtonDefinition
 * @see GenericSelectMenuDefinition GenericSelectMenuDefinition
 * @since 4.0.0
 */
public abstract sealed class GenericComponentDefinition extends EphemeralInteractionDefinition implements CustomId
        permits ButtonDefinition, GenericSelectMenuDefinition {

    protected GenericComponentDefinition(Method method, Set<String> permissions, ReplyConfig replyConfig) {
        super(method, permissions, replyConfig);
    }

    @Override
    public @NotNull String boundCustomId(@NotNull String runtimeId) {
        return "%s.%s.%s".formatted(PREFIX, runtimeId, definitionId);
    }

    @Override
    public @NotNull String independentCustomId() {
        return "%s.independent.%s".formatted(PREFIX, definitionId);
    }
}
