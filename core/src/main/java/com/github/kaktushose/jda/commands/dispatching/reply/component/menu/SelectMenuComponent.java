package com.github.kaktushose.jda.commands.dispatching.reply.component.menu;

import com.github.kaktushose.jda.commands.definitions.interactions.component.ComponentDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.SelectMenuDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.Component;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/// An implementation of [Component] specific to [SelectMenu].
///
/// Dynamic registration of menu options is supported by [EntitySelectMenuComponent] and [StringSelectComponent].
///
/// @param <S> the concrete subtype of [SelectMenuComponent]
/// @param <T> the type of [SelectMenu] the [SelectMenuDefinition] represents
/// @param <D> the type of [SelectMenuDefinition] this [SelectMenuComponent] represents
/// @see EntitySelectMenuComponent
/// @see StringSelectComponent
public sealed abstract class SelectMenuComponent<S extends SelectMenuComponent<S, T, D>,
        T extends SelectMenu, D extends SelectMenuDefinition<T>> extends Component<S, T, D>
        permits StringSelectComponent, EntitySelectMenuComponent {

    protected String placeholder;

    // wrapper types for nullability (null -> not set)
    protected Integer minValues;
    protected Integer maxValues;

    public SelectMenuComponent(@NotNull String method, @NotNull Class<?> origin) {
        super(method, origin);
    }

    /// @see SelectMenu.Builder#setPlaceholder(String)
    public S placeholder(@Nullable String placeholder) {
        this.placeholder = placeholder;
        return self();
    }

    /// @see SelectMenu.Builder#setMinValues(int)
    public S minValues(int minValue) {
        this.minValues = minValue;
        return self();
    }

    /// @see SelectMenu.Builder#setMaxValues(int)
    public S maxValues(int maxValue) {
        this.maxValues = maxValue;
        return self();
    }

    /// @see SelectMenu.Builder#setRequiredRange(int, int)
    public S requiresRange(int min, int max) {
        minValues(min);
        maxValues(max);
        return self();
    }
}
