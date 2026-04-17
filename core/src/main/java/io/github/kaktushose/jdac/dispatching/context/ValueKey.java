package io.github.kaktushose.jdac.dispatching.context;

/// Represents a key for a value in a [KeyValueStore].
/// The key provides a way to uniquely identify a value in the store.
/// The key also defined the class the value in the [KeyValueStore] should be.
public record ValueKey<T>(String key, Class<T> clazz) {
    /// Checks whether the class of the object is assignable to the class defined in the key.
    public boolean isAssignable(Object object) {
        return clazz().isAssignableFrom(object.getClass());
    }

    public T cast(Object object) {
        return clazz.cast(object);
    }
}
