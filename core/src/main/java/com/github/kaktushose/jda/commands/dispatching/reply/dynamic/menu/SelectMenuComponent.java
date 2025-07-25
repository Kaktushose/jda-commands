package com.github.kaktushose.jda.commands.dispatching.reply.dynamic.menu;

import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.SelectMenuDefinition;
import com.github.kaktushose.jda.commands.dispatching.reply.Component;
import com.github.kaktushose.jda.commands.i18n.I18n;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.jspecify.annotations.Nullable;

/// An implementation of [Component] specific to [SelectMenu].
///
/// Dynamic registration of menu options is supported by [EntitySelectMenuComponent] and [StringSelectComponent].
///
/// @param <S> the concrete subtype of [SelectMenuComponent]
/// @param <T> the type of [SelectMenu] the [SelectMenuDefinition] represents
/// @param <B> the type of [SelectMenu.Builder]
/// @param <D> the type of [SelectMenuDefinition] this [SelectMenuComponent] represents
/// @see EntitySelectMenuComponent
/// @see StringSelectComponent
public sealed abstract class SelectMenuComponent<S extends SelectMenuComponent<S, T, B, D>,
        T extends SelectMenu, B extends SelectMenu.Builder<T, B>, D extends SelectMenuDefinition<T>> extends Component<S, T, B, D>
        permits StringSelectComponent, EntitySelectMenuComponent {

    protected @Nullable String placeholder;

    // wrapper types for nullability (null -> not set)
    protected @Nullable Integer minValues;
    protected @Nullable Integer maxValues;

    public SelectMenuComponent(String method, @Nullable Class<?> origin, I18n.Entry[] placeholder) {
        super(method, origin, placeholder);
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
