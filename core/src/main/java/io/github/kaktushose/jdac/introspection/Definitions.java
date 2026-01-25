package io.github.kaktushose.jdac.introspection;

import io.github.kaktushose.jdac.definitions.Definition;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;

import java.util.Collection;
import java.util.SequencedCollection;
import java.util.function.Predicate;

/// This interface provides read-only access to the indexed [Definition]s which are used to build and execute interactions.
///
/// This may be useful if you want to build a help command for example or get access to individual metadata annotation on
/// your interaction controller classes (by using [InteractionDefinition#classDescription]).
public sealed interface Definitions permits InteractionRegistry {


    /// Gets all [InteractionDefinition]s that were indexed by JDA-Commands.
    ///
    /// @return an _immutable_ collection of all indexes [InteractionDefinition]s
    Collection<InteractionDefinition> all();

    /// Gets a subset of all registered [InteractionDefinition] filtered by their [type][Object#getClass()]
    /// and the passed [Predicate].
    ///
    /// @param type the [type][Object#getClass()] of the needed [InteractionDefinition]s
    /// @param predicate the [Predicate] to filter the [InteractionDefinition]s of the passed type
    ///
    /// @return the [InteractionDefinition]s matching the provided criteria
    <T extends Definition> SequencedCollection<T> find(Class<T> type, Predicate<T> predicate);

    /// Gets the first [InteractionDefinition] with the given [type][Object#getClass()] that
    /// match the passed [Predicate]
    ///
    /// @param type the [type][Object#getClass()] of the needed [InteractionDefinition]
    /// @param predicate the [Predicate] that has to match the needed [InteractionDefinition]
    ///
    /// @return the frist [InteractionDefinition] matching the provided criteria
    <T extends Definition> T findFirst(Class<T> type, Predicate<T> predicate);

    /// Gets all registered [InteractionDefinition]s matching the passed type
    ///
    /// @param type the [type][Object#getClass()] of the needed [InteractionDefinition]
    ///
    /// @return the [InteractionDefinition]s matching the provided type
    default <T extends Definition> SequencedCollection<T> find(Class<T> type) {
        return find(type, _ -> true);
    }
}
