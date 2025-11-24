package io.github.kaktushose.jdac.configuration.internal;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.PropertyProvider;
import io.github.kaktushose.jdac.exceptions.ConfigurationException;

import java.util.*;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

public class Properties {
    public static ScopedValue<Boolean> INSIDE_FRAMEWORK = ScopedValue.newInstance();

    public static final int FALLBACK_PRIORITY = 0;
    public static final int USER_PRIORITY = Integer.MAX_VALUE;

    private final Map<Property<?>, SortedSet<PropertyProvider<?>>> properties = new HashMap<>();

    private void checkCategory(PropertyProvider<?> provider) {
        int priority = provider.priority();

        if (!INSIDE_FRAMEWORK.isBound() && (priority <= 100 || priority == Integer.MAX_VALUE)) {
            throw new ConfigurationException("reserved-priority", entry("priority", priority));
        }

        switch (provider.type().category()) {
            case PROVIDED -> {
                if (priority != Properties.FALLBACK_PRIORITY) {
                    throw new ConfigurationException("provided-property", entry("property", provider.type().name()));
                }
            }
            case USER -> {
                if (priority != Properties.USER_PRIORITY && priority != Properties.FALLBACK_PRIORITY) {
                    throw new ConfigurationException("user-property", entry("property", provider.type().name()));
                }
            }
        }
    }

    public <T> void add(PropertyProvider<T> provider) {
        int priority = provider.priority();
        if (priority < 0) {
            throw new ConfigurationException("negative-priority", entry("priority", priority));
        }

        checkCategory(provider);

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
