package io.github.kaktushose.jdac.configuration.type;

import io.github.kaktushose.jdac.configuration.PropertyType;

import java.util.Collection;

public record Enumeration<E>(String name, PropertyType.Scope scope, Class<E> type,
                             FallbackBehaviour fallbackBehaviour) implements PropertyType<Collection<E>> {}
