package io.github.kaktushose.jdac.definitions.interactions.component;

import io.github.kaktushose.jdac.definitions.description.ClassDescription;
import io.github.kaktushose.jdac.definitions.description.MethodDescription;
import io.github.kaktushose.jdac.definitions.features.CustomIdJDAEntity;
import io.github.kaktushose.jdac.definitions.features.JDAEntity;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.SelectMenuDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.StringSelectMenuDefinition;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

/// Common interface for component interaction definitions.
///
/// @see ButtonDefinition
/// @see EntitySelectMenuDefinition
/// @see StringSelectMenuDefinition
public sealed interface ComponentDefinition<T> extends InteractionDefinition, JDAEntity<T>, CustomIdJDAEntity<T>
        permits ButtonDefinition, SelectMenuDefinition {

    /// Overrides the oldValue with the newValue if present, else returns the oldValue
    ///
    /// @param <T> the type of the value
    /// @return if present the newValue, else the oldValue
    @Nullable
    static <T> T override(@Nullable T oldValue, @Nullable T newValue) {
        return newValue != null
                ? newValue
                : oldValue;
    }

    /// If present adds the newValues to the collection provided by the supplier, else returns the oldValue.
    ///
    /// @param <E> the type of elements in the collection
    /// @param <T> the type of the collection
    /// @return if present the newValue, else the oldValue
    static <E, T extends Collection<E>> T override(Supplier<T> newSupp, T oldValue, @Nullable T newValues) {
        if (newValues == null) return oldValue;

        T collection = newSupp.get();
        collection.addAll(oldValue);
        collection.addAll(newValues);
        return collection;
    }

    /// The [ClassDescription] of the declaring class of the [#methodDescription()]
    ClassDescription classDescription();

    /// The [MethodDescription] of the method this definition is bound to
    MethodDescription methodDescription();

    /// The uniqueId of this component. Default value is `-1` which will result in Discord assigning an id.
    int uniqueId();

}
