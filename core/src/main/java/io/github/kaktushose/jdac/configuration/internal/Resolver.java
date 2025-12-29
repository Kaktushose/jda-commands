package io.github.kaktushose.jdac.configuration.internal;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.PropertyProvider;
import io.github.kaktushose.jdac.exceptions.ConfigurationException;
import io.github.kaktushose.jdac.internal.Helpers;
import io.github.kaktushose.jdac.internal.logging.JDACLogger;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

@ApiStatus.Internal
public final class Resolver {

    private static final Logger log = JDACLogger.getLogger(Resolver.class);

    private final Map<Property<?>, Object> cache = new HashMap<>();
    private final Map<Property<?>, SortedSet<PropertyProvider<?>>> properties;

    private final Executor executor;

    public Resolver(Map<Property<?>, SortedSet<PropertyProvider<?>>> properties) {
        this.properties = properties;
        this.executor = new Executor(this);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Property<T> type) {
        if (cache.containsKey(type)) return (T) cache.get(type);

        // safe by invariant
        SortedSet<PropertyProvider<T>> providers = Helpers.castUnsafe(properties.getOrDefault(type, new TreeSet<>()));

        Result<T> result = switch (type) {
            case Property.Singleton<T> _ -> handleOne(providers, type);
            case Property.Enumeration<?> _ -> (Result<T>) handleMany(Helpers.<SortedSet<PropertyProvider<Collection<Object>>>>castUnsafe(providers), ArrayList::new, List::addAll);
            case Property.Map<?, ?> _ -> (Result<T>) handleMany(Helpers.<SortedSet<PropertyProvider<Map<Object, Object>>>>castUnsafe(providers), HashMap::new, java.util.Map::putAll);
        };

        log.debug("Property {} got from provider(s) in {}", type.name(), result.refClasses.stream().map(Class::getName).collect(Collectors.joining(",")));

        cache.put(type, result.value);

        return result.value;
    }

    public Resolver createSub(Properties additional) {
        Resolver sub = new Resolver(new HashMap<>(this.properties));
        sub.properties.putAll(additional.properties());
        sub.cache.putAll(this.cache);
        return sub;
    }

    private <T> boolean shouldSkip(SortedSet<PropertyProvider<T>> providers, PropertyProvider<T> provider) {
        return providers.size() > 1
                && provider.priority() == Properties.FALLBACK_PRIORITY
                && provider.type().fallbackBehaviour() == Property.FallbackBehaviour.OVERRIDE;
    }

    private <T> Result<T> handleOne(SortedSet<PropertyProvider<T>> providers, Property<T> type) {
        return providers.stream()
                .flatMap(provider -> {
                    T obj = executor.applyProvider(provider);
                    return obj == null ? Stream.empty() : Stream.of(new Result<>(obj, List.of(provider.referenceClass())));
                })
                .findFirst()
                .orElseThrow(() -> new ConfigurationException("property-not-set", entry("property", type.name())));
    }

    private <T, B extends T> Result<T> handleMany(SortedSet<PropertyProvider<T>> providers, Supplier<B> collectionSup, BiConsumer<B, T> adder) {
        Collection<Class<?>> usedProviders = new ArrayList<>();

        B collection = collectionSup.get();
        for (PropertyProvider<T> provider : providers) {
            if (shouldSkip(providers, provider)) {
                continue;
            }

            T applied = executor.applyProvider(provider);
            if (applied == null) continue;
            adder.accept(collection, applied);
            usedProviders.add(provider.referenceClass());
        }

        return new Result<>(collection, usedProviders);
    }

    private record Result<T>(T value, Collection<Class<?>> refClasses) {}
}
