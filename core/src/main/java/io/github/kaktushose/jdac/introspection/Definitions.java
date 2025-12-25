package io.github.kaktushose.jdac.introspection;

import io.github.kaktushose.jdac.definitions.Definition;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;

import java.util.Collection;
import java.util.function.Predicate;

public sealed interface Definitions permits InteractionRegistry {
    Collection<Definition> all();
    <T extends Definition> Collection<T> find(Class<T> type, Predicate<T> predicate);
    <T extends Definition> T findFirst(Class<T> type, Predicate<T> predicate);
}
