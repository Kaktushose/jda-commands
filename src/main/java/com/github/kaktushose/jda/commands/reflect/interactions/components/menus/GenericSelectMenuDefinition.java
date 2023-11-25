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
public abstract class GenericSelectMenuDefinition<T extends SelectMenu> extends GenericComponentDefinition {

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
     * @param id      the id of the component
     * @param enabled {@code true} if the component should be enabled
     * @return the select menu component
     */
    public abstract T toSelectMenu(String id, boolean enabled);

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
