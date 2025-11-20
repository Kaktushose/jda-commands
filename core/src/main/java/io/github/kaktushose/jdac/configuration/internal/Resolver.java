package io.github.kaktushose.jdac.configuration.internal;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.PropertyProvider;
import io.github.kaktushose.jdac.exceptions.ConfigurationException;
import io.github.kaktushose.jdac.internal.Helpers;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

public final class Resolver {
    private static final ScopedValue<List<Property<?>>> STACK = ScopedValue.newInstance();

    private final Map<Property<?>, Object> cache = new HashMap<>();
    private final Map<Property<?>, SortedSet<PropertyProvider<?>>> properties;

    Resolver(Map<Property<?>, SortedSet<PropertyProvider<?>>> properties) {
        this.properties = properties;
    }

    public <T> T get(Property<T> type) {
        if (STACK.isBound()) {
            List<Property<?>> stack = STACK.get();
            if (stack.contains(type)) {
                throw new UnsupportedOperationException("add good exception: cycling dependency detected %s".formatted(type));
            }

            stack.add(type);
            T result = resolve(type);
            stack.removeLast();
            return result;
        } else {
            return ScopedValue.where(STACK, new ArrayList<>(List.of(type))).call(() -> resolve(type));
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T resolve(Property<T> type) {
        if (cache.containsKey(type)) return (T) cache.get(type);

        // safe by invariant
        SortedSet<PropertyProvider<T>> providers = Helpers.castUnsafe(properties.getOrDefault(type, new TreeSet<>()));

        T result = switch (type) {
            case Property.Singleton<T> _ -> handleOne(providers, type);
            case Property.Enumeration<?> _ -> (T) handleMany(Helpers.<SortedSet<PropertyProvider<Collection<Object>>>>castUnsafe(providers), ArrayList::new, List::addAll);
            case Property.Mapping<?, ?> _ -> (T) handleMany(Helpers.<SortedSet<PropertyProvider<Map<Object, Object>>>>castUnsafe(providers), HashMap::new, Map::putAll);
        };

        cache.put(type, result);

        return result;
    }

    private <T> boolean shouldSkip(SortedSet<PropertyProvider<T>> providers, PropertyProvider<T> provider) {
        return providers.size() > 1
                && provider.priority() == Properties.FALLBACK_PRIORITY
                && provider.type().fallbackBehaviour() == Property.FallbackBehaviour.OVERRIDE;
    }

    @NonNull
    private <T> T handleOne(SortedSet<PropertyProvider<T>> providers, Property<T> type) {
        return providers.stream()
                .map(provider -> provider.supplier().apply(this::get))
                .filter(Objects::nonNull) // intellij doesn't understand the null check here -> Objects#requireNonNull
                .findFirst()
                .orElseThrow(() -> new ConfigurationException("property-not-set", entry("property", type.name())));
    }

    @NonNull
    private <T, B extends T> T handleMany(SortedSet<PropertyProvider<T>> providers, Supplier<B> collectionSup, BiConsumer<B, T> adder) {
        B collection = collectionSup.get();
        for (PropertyProvider<T> provider : providers) {
            if (shouldSkip(providers, provider)) {
                continue;
            }

            T applied = provider.supplier().apply(this::get);
            if (applied == null) continue;
            adder.accept(collection, applied);
        }

        return collection;
    }

}
