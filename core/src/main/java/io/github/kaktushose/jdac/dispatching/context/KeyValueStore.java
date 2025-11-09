package io.github.kaktushose.jdac.dispatching.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/// A simple key-value-store to store variables between events.
public class KeyValueStore {

    private final Map<String, Object> values = new HashMap<>();

    /// Gets a value.
    ///
    /// @param key   the key
    /// @param clazz the class of the value
    /// @param <T>   the type of the value
    /// @return an [Optional] holding the value
    public <T> Optional<T> get(String key, Class<? extends T> clazz) {
        return Optional.ofNullable(values.get(key)).filter(it -> it.getClass().isAssignableFrom(clazz)).map(clazz::cast);
    }

    /// Associates the specified value with the specified key.
    ///
    /// @param key   the key
    /// @param value the value
    /// @return this instance for fluent interface
    public KeyValueStore put(String key, Object value) {
        values.put(key, value);
        return this;
    }

    /// Whether this StateSection has a value mapped to the key.
    ///
    /// @param key the key
    /// @return `true` if this StateSection has a value mapped to the key
    public boolean contains(String key) {
        return values.containsKey(key);
    }

    /// Removes the value mapping for a key.
    ///
    /// @param key key whose mapping is to be removed
    /// @return this instance for fluent interface
    public KeyValueStore remove(String key) {
        values.remove(key);
        return this;
    }

    /// Removes all the value mappings.
    ///
    /// @return this instance for fluent interface
    public KeyValueStore clear() {
        values.clear();
        return this;
    }
}
