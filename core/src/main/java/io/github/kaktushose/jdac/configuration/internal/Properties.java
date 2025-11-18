package io.github.kaktushose.jdac.configuration.internal;

import io.github.kaktushose.jdac.configuration.PropertyProvider;
import io.github.kaktushose.jdac.configuration.PropertyType;

import java.util.*;

public class Properties {
    private final Map<PropertyType<?>, SortedSet<PropertyProvider<?>>> properties = new HashMap<>();

    public <T> void add(PropertyProvider<T> provider) {
        properties.computeIfAbsent(provider.type(), _ -> new TreeSet<>()).add(provider);
    }

    public void addAll(Collection<PropertyProvider<?>> provider) {
        provider.forEach(this::add);
    }

    public Resolver createResolver() {
        Extensions extensions = new Extensions();
        extensions.load(new Resolver(properties)); // Resolver just for user settable types
        extensions.register(this);

        return new Resolver(properties);
    }
}
