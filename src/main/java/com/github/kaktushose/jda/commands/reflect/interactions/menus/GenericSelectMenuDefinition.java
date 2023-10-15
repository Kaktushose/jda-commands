package com.github.kaktushose.jda.commands.reflect.interactions.menus;

import com.github.kaktushose.jda.commands.dispatching.interactions.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandContext;
import com.github.kaktushose.jda.commands.reflect.interactions.EphemeralInteraction;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * Abstract base class for select menus.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see EntitySelectMenuDefinition
 * @see StringSelectMenuDefinition
 * @since 4.0.0
 */
public abstract class GenericSelectMenuDefinition<T extends SelectMenu> extends EphemeralInteraction {

    protected final String placeholder;
    protected final int minValue;
    protected final int maxValue;

    protected GenericSelectMenuDefinition(Method method, boolean ephemeral, String placeholder, int minValue, int maxValue) {
        super(method, ephemeral);
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

    /**
     * Gets the runtime id. The runtime id is composed of the static interaction id and the
     * snowflake id of the interaction event that created the runtime.
     *
     * @param context the {@link CommandContext} this button will be attached to
     * @return the runtime id
     */
    @NotNull
    public String getRuntimeId(GenericContext<? extends GenericInteractionCreateEvent> context) {
        return String.format("%s.%s", getId(), context.getRuntime().getInstanceId());
    }

}
