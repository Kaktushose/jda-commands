package com.github.kaktushose.jda.commands.definitions.interactions.component;

import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.features.CustomIdJDAEntity;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.SelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

/// Common interface for component interaction definitions.
///
/// @see ButtonDefinition
/// @see EntitySelectMenuDefinition
/// @see StringSelectMenuDefinition
public sealed interface ComponentDefinition<T> extends InteractionDefinition, JDAEntity<T>, CustomIdJDAEntity<T>
        permits ButtonDefinition, SelectMenuDefinition {

    /// The [ClassDescription] of the declaring class of the [#methodDescription()]
    @NotNull ClassDescription classDescription();

    /// The [MethodDescription] of the method this definition is bound to
    @NotNull MethodDescription methodDescription();

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
    @NotNull
    static <E, T extends Collection<E>> T override(@NotNull Supplier<T> newSupp, @NotNull T oldValue, @Nullable T newValues) {
        if (newValues == null) return oldValue;

        T collection = newSupp.get();
        collection.addAll(oldValue);
        collection.addAll(newValues);
        return collection;
    }

}
