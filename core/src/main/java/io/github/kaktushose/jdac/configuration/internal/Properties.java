package io.github.kaktushose.jdac.configuration.internal;

import io.github.kaktushose.jdac.configuration.PropertyProvider;
import io.github.kaktushose.jdac.configuration.PropertyType;

import java.util.*;

public class Properties {
    public static ScopedValue<Boolean> INSIDE_FRAMEWORK = ScopedValue.newInstance();

    public static final int FALLBACK_PRIORITY = 0;
    public static final int USER_PRIORITY = Integer.MAX_VALUE;

    private final Map<PropertyType<?>, SortedSet<PropertyProvider<?>>> properties = new HashMap<>();

    private void checkScope(PropertyProvider<?> provider) {
        int priority = provider.priority();

        if (!INSIDE_FRAMEWORK.isBound() && (priority <= 100 || priority == Integer.MAX_VALUE)) {
            throw new UnsupportedOperationException("add exception: priorities 0 - 10 are reserved");
        }

        switch (provider.type().scope()) {
            case PROVIDED -> {
                if (priority != Properties.FALLBACK_PRIORITY) {
                    throw new UnsupportedOperationException("add exception: property can only be provided by the framework %s".formatted(provider.type()));
                }
            }
            case USER -> {
                if (priority != Properties.USER_PRIORITY && priority != Properties.FALLBACK_PRIORITY) {
                    throw new UnsupportedOperationException("add exception: property can only be provided by user %s".formatted(provider.type()));
                }
            }
        }
    }

    public <T> void add(PropertyProvider<T> provider) {
        if (provider.priority() < 0) {
            throw new UnsupportedOperationException("add exception: priority can't be negative");
        }

        checkScope(provider);

        properties.computeIfAbsent(provider.type(), _ -> new TreeSet<>()).add(provider);
    }

    public void addAll(Collection<PropertyProvider<?>> provider) {
        provider.forEach(this::add);
    }

    public Resolver createResolver() {
        ExtensionLoader extensionLoader = new ExtensionLoader();
        extensionLoader.load(new Resolver(properties)); // Resolver just for user settable types
        extensionLoader.register(this);

        return new Resolver(properties);
    }
}
