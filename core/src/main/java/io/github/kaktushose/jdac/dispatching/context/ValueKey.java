package io.github.kaktushose.jdac.dispatching.context;

/// Represents a key for a value in a [KeyValueStore].
/// The key provides a way to uniquely identify a value in the store.
/// The key also defines the class that the value in the [KeyValueStore] should be.
/// @param <T> Type of the object that can be set and retrieved with this key
/// @param key Key of the value in the [KeyValueStore]
/// @param clazz Class of the object that can be set and retrieved with this key
public record ValueKey<T>(String key, Class<T> clazz) {
    /// Checks whether the class of the object is assignable to the class defined in the key.
    /// @param object Object to check
    /// @return true if the class of the object is assignable to the class defined in the key, false otherwise
    public boolean isAssignable(Object object) {
        return clazz().isAssignableFrom(object.getClass());
    }

    public T cast(Object object) {
        return clazz.cast(object);
    }
}
