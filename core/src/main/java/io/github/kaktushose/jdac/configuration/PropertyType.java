package io.github.kaktushose.jdac.configuration;

import java.util.Collection;
import java.util.Map;

public sealed interface PropertyType<T> {
    record Mapping<K, V>(String name, Class<K> key, Class<V> value, FallbackBehaviour fallbackBehaviour) implements PropertyType<Map<K, V>> {}
    record Enumeration<E>(String name, Class<E> type, FallbackBehaviour fallbackBehaviour) implements PropertyType<Collection<E>> {}
    record Instance<T>(String name, Class<T> type) implements PropertyType<T> {
        @Override
        public FallbackBehaviour fallbackBehaviour() {
            throw new UnsupportedOperationException("fallback behaviour not supported on PropertyType.Instance");
        }
    }

    FallbackBehaviour fallbackBehaviour();
    String name();

    enum FallbackBehaviour {
        OVERRIDE,
        ACCUMULATE
    }
}
