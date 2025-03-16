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

    static <T> T override(T oldValue, T newValue) {
        return newValue != null
                ? newValue
                : oldValue;
    }

    static <E, T extends Collection<E>> T override(Supplier<T> newSupp, T oldValue, T newValues) {
        if (newValues == null) return oldValue;

        if (oldValue != null) {
            T collection = newSupp.get();
            collection.addAll(oldValue);
            collection.addAll(newValues);
            return collection;
        }
        return newValues;
    }

}
