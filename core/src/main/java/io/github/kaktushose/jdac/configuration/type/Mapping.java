package io.github.kaktushose.jdac.configuration.type;

import io.github.kaktushose.jdac.configuration.PropertyType;

import java.util.Map;

public record Mapping<K, V>(String name, PropertyType.Scope scope, Class<K> key, Class<V> value,
                            FallbackBehaviour fallbackBehaviour) implements PropertyType<Map<K, V>> {}
