package io.github.kaktushose.jdac.configuration.internal;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.PropertyProvider;
import io.github.kaktushose.jdac.exceptions.ConfigurationException;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.introspection.internal.IntrospectionImpl;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

@ApiStatus.Internal
public class Properties {

    public static final int FALLBACK_PRIORITY = 0;
    public static final int USER_PRIORITY = Integer.MAX_VALUE;
    public static ScopedValue<Boolean> INSIDE_FRAMEWORK = ScopedValue.newInstance();
    private final Map<Property<?>, SortedSet<PropertyProvider<?>>> properties = new HashMap<>();

    private void checkCategory(PropertyProvider<?> provider) {
        int priority = provider.priority();

        if (!INSIDE_FRAMEWORK.orElse(false) && (priority <= 100 || priority == Integer.MAX_VALUE)) {
            throw new ConfigurationException("reserved-priority", entry("priority", priority));
        }

        switch (provider.type().category()) {
            case PROVIDED -> {
                if (priority != Properties.FALLBACK_PRIORITY) {
                    throw new ConfigurationException("provided-property", entry("property", provider.type().name()));
                }
            }
            case USER_SETTABLE -> {
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
        return new Resolver(this.properties);
    }

    Map<Property<?>, SortedSet<PropertyProvider<?>>> properties() {
        return properties;
    }

    public static class Builder {
        private final Properties properties = new Properties();
        private boolean restricted = false;

        public static Builder newRestricted() {
            return new Builder().restricted(true);
        }

        public Builder restricted(boolean restricted) {
            this.restricted = restricted;
            return this;
        }

        public <T> Builder addFallback(Property<T> type, Function<PropertyProvider.Context, T> supplier) {
            ScopedValue.where(Properties.INSIDE_FRAMEWORK, restricted)
                    .run(() -> properties.add(PropertyProvider.create(type, Properties.FALLBACK_PRIORITY, supplier)));
            return this;
        }

        public Properties build() {
            return properties;
        }

        public IntrospectionImpl createIntrospection(IntrospectionImpl introspection, Stage stage) {
            return introspection.createSub(properties, stage);
        }
    }
}
