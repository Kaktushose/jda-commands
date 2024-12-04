package com.github.kaktushose.jda.commands.reflect.interactions.components.menus;

import com.github.kaktushose.jda.commands.reflect.interactions.components.GenericComponentDefinition;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Abstract base class for select menus.
 *
 * @see EntitySelectMenuDefinition
 * @see StringSelectMenuDefinition
 * @since 4.0.0
 */
public abstract sealed class GenericSelectMenuDefinition<T extends SelectMenu> extends GenericComponentDefinition permits EntitySelectMenuDefinition, StringSelectMenuDefinition {

    protected final String placeholder;
    protected final int minValue;
    protected final int maxValue;

    protected GenericSelectMenuDefinition(Method method, Set<String> permissions, boolean ephemeral, String placeholder, int minValue, int maxValue) {
        super(method, permissions, ephemeral);
        this.placeholder = placeholder;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Transforms this definition to a select menu component.
     *
     * @param runtimeId the runtimeId of the
     *                  {@link com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor.InteractionRuntime InteractionRuntime}
     *                  of this interaction execution
     * @param enabled   {@code true} if the component should be enabled
     * @return the select menu component
     */
    public abstract T toSelectMenu(String runtimeId, boolean enabled);

    /**
     * Gets the placeholder string.
     *
     * @return the placeholder string
     */
    public String getPlaceholder() {
        return placeholder;
    }

    /**
     * Gets the minimum value.
     *
     * @return the minimum value
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * Gets the maximum value.
     *
     * @return the maximum value
     */
    public int getMaxValue() {
        return maxValue;
    }

    @Override
    public String getDisplayName() {
        return placeholder.isEmpty() ? "Select Menu" : placeholder;
    }
}
