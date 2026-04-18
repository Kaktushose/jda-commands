package io.github.kaktushose.jdac.dispatching.reply.dynamic.menu;

import io.github.kaktushose.jdac.definitions.interactions.component.menu.SelectMenuDefinition;
import io.github.kaktushose.jdac.dispatching.reply.Component;
import io.github.kaktushose.jdac.message.placeholder.Entry;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.components.selections.SelectMenu.Builder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

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
public abstract sealed class SelectMenuComponent<S extends SelectMenuComponent<S, T, B, D>,
        T extends SelectMenu, B extends Builder<T, B>, D extends SelectMenuDefinition<T>> extends Component<S, T, B, D>
        implements SelectMenu
        permits StringSelectComponent, EntitySelectMenuComponent {

    protected @Nullable String placeholder;

    // wrapper types for nullability (null -> not set)
    protected @Nullable Integer minValues;
    protected @Nullable Integer maxValues;
    protected @Nullable Boolean required;

    public SelectMenuComponent(String method, @Nullable Class<?> origin, Entry[] placeholder) {
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

    /// @see SelectMenu.Builder#setRequired(Boolean)
    public S required(@Nullable Boolean required) {
        this.required = required;
        return self();
    }

    @Override
    public boolean isDisabled() {
        return !enabled();
    }

    @Override
    public @NonNull S withDisabled(boolean disabled) {
        super.enabled(!disabled);
        return self();
    }

    @Override
    public @Nullable String getCustomId() {
        return null;
    }

    @Override
    public @Nullable String getPlaceholder() {
        return placeholder;
    }

    @Override
    public int getMinValues() {
        return Objects.requireNonNullElse(minValues, -1);
    }

    @Override
    public int getMaxValues() {
        return Objects.requireNonNullElse(maxValues, -1);
    }

    @Override
    public @Nullable Boolean isRequired() {
        return required;
    }

    @Override
    public @NonNull B createCopy() {
        throw new UnsupportedOperationException();
    }
}
