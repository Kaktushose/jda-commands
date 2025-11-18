package io.github.kaktushose.jdac.configuration;

import java.util.Collection;
import java.util.Map;

public record PropertyType<T>(
        ValueType<T> valueType,
        FallbackBehaviour fallbackBehaviour
) {

    public sealed interface ValueType<T> {
        record Mapping<K, V>(Class<K> key, Class<V> value) implements ValueType<Map<K, V>> {}
        record Enumeration<E>(Class<E> type) implements ValueType<Collection<E>> {}
        record Instance<T>(Class<T> type) implements ValueType<T> {}
    }

    public enum FallbackBehaviour {
        OVERRIDE,
        ACCUMULATE
    }
}
