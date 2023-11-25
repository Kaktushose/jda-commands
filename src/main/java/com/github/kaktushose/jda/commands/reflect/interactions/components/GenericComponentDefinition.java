package com.github.kaktushose.jda.commands.reflect.interactions.components;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.SlashCommandContext;
import com.github.kaktushose.jda.commands.reflect.interactions.EphemeralInteractionDefinition;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Marker class for component definitions.
 *
 * @see ButtonDefinition
 * @see com.github.kaktushose.jda.commands.reflect.interactions.components.menus.GenericSelectMenuDefinition GenericSelectMenuDefinition
 * @since 4.0.0
 */
public abstract class GenericComponentDefinition extends EphemeralInteractionDefinition {

    protected GenericComponentDefinition(Method method, Set<String> permissions, boolean ephemeral) {
        super(method, permissions, ephemeral);
    }

    /**
     * Gets the runtime id. The runtime id is composed of the static interaction id and the
     * snowflake id of the interaction event that created the runtime.
     *
     * @param context the {@link SlashCommandContext} this button will be attached to
     * @return the runtime id
     */
    @NotNull
    public String getRuntimeId(Context context) {
        return String.format("%s.%s", getId(), context.getRuntime().getInstanceId());
    }

}
