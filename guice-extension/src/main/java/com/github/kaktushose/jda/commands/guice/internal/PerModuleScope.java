package com.github.kaktushose.jda.commands.guice.internal;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public class PerModuleScope implements Scope {

    private final Map<Key<?>, Object> store = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
        return () -> (T) store.computeIfAbsent(key, _ -> unscoped.get());
    }
}
