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
        return Optional.ofNullable(values.get(key)).filter(it -> clazz.isAssignableFrom(it.getClass())).map(clazz::cast);
    }

    /// Gets a value.
    ///
    /// @param key   the key
    /// @param clazz the class of the value
    /// @param <T>   the type of the value
    /// @return an [Optional] holding the value
    /// @throws java.util.NoSuchElementException if no value is present
    public <T> T getOrThrow(String key, Class<? extends T> clazz) {
        Optional<T> value = get(key, clazz); // Line is required for proper type inference
        return value.orElseThrow();
    }

    /// Gets a value.
    ///
    /// @param key the key
    /// @param <T> the type of the value
    /// @return an [Optional] holding the value
    /// @throws ClassCastException if the value cannot be cast to the required class
    public <T> Optional<T> get(String key) {
        return Optional.ofNullable((T) values.get(key));
    }
    /// Gets a value.
    ///
    /// @param key the key
    /// @param <T> the type of the value
    /// @return an [Optional] holding the value
    /// @throws ClassCastException if the value cannot be cast to the required class
    /// @throws java.util.NoSuchElementException if no value is present
    public <T> T getOrThrow(String key) {
        Optional<T> o = get(key); // Line is required for proper type inference
        return o.orElseThrow();
    }

    /// Gets a value.
    ///
    /// @param key the key
    /// @param <T> the type of the value
    /// @return an [Optional] holding the value
    public <T> Optional<T> get(ValueKey<T> key) {
        return Optional.ofNullable(values.get(key.key())).filter(key::isAssignable).map(key::cast);
    }

    /// Gets a value.
    ///
    /// @param key the key
    /// @param <T> the type of the value
    /// @return an [Optional] holding the value
    /// @throws java.util.NoSuchElementException if no value is present
    public <T> T getOrThrow(ValueKey<T> key) {
        Optional<T> value = get(key.key()); // Line is required for proper type inference
        return value.orElseThrow();
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

    /// Associates the specified value with the specified key.
    ///
    /// @param key   the key
    /// @param value the value
    /// @return this instance for fluent interface
    public <T> KeyValueStore put(ValueKey<T> key, T value) {
        values.put(key.key(), value);
        return this;
    }

    /// Whether this StateSection has a value mapped to the key.
    ///
    /// @param key the key
    /// @return `true` if this StateSection has a value mapped to the key
    public boolean contains(String key) {
        return values.containsKey(key);
    }

    /// Whether this StateSection has a value with the correct type mapped to the key.
    ///
    /// @param key the key
    /// @return `true` if this StateSection has a value mapped to the key and has the correct type
    public <T> boolean contains(ValueKey<T> key) {
        return get(key).isPresent();
    }

    /// Removes the value mapping for a key.
    ///
    /// @param key key whose mapping is to be removed
    /// @return this instance for fluent interface
    public KeyValueStore remove(String key) {
        values.remove(key);
        return this;
    }

    /// Removes the value mapping for a key.
    ///
    /// @param key key whose mapping is to be removed
    /// @return this instance for fluent interface
    public <T> KeyValueStore remove(ValueKey<T> key) {
        values.remove(key.key());
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
