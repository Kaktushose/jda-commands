package io.github.kaktushose.jdac.guice.internal.guice;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.introspection.Introspection;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public class RuntimeBoundScope implements Scope {

    private final Map<String, Map<Key<?>, Object>> store = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
        return () -> {
            Map<Key<?>, Object> runtimeBoundCache =
                    store.computeIfAbsent(Introspection.scopedGet(Property.RUNTIME_ID), _ -> new ConcurrentHashMap<>());

            // runtimeBundCache is never accessed concurrently, that's fine
            // cannot use computeIfAbsent, will throw recursive update
            if (!runtimeBoundCache.containsKey(key)) {
                T val = unscoped.get();
                runtimeBoundCache.put(key, val);
                return val;
            }
            return (T) runtimeBoundCache.get(key);
        };
    }

    public void removeRuntime(String id) {
        store.remove(id);
    }
}
