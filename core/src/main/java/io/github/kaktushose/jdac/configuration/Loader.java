package io.github.kaktushose.jdac.configuration;

import java.util.*;

public class Loader {
    private final Map<PropertyType<?>, SortedSet<PropertyProvider<?>>> properties;

    public Loader(Map<PropertyType<?>, SortedSet<PropertyProvider<?>>> properties) {
        this.properties = properties;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(PropertyType<T> type) {
        SortedSet<PropertyProvider<?>> providers = properties.get(type);
        if (providers == null) throw new UnsupportedOperationException("Add proper exception: value not set");

        return switch (type.valueType()) {
            case PropertyType.ValueType.Instance(var _) -> ((PropertyProvider<T>) providers.getLast()).supplier().apply(this::get);
            case PropertyType.ValueType.Enumeration<?> _ -> handleEnumeration(providers, type);
            case PropertyType.ValueType.Mapping<?, ?> _ -> handleMapping(providers, type);
        };
    }

    private boolean shouldSkip(SortedSet<PropertyProvider<?>> providers, PropertyProvider<?> provider) {
        return providers.size() > 1
                && provider.priority() == PropertyProvider.FALLBACK_PRIORITY
                && provider.type().fallbackBehaviour() == PropertyType.FallbackBehaviour.OVERRIDE;
    }

    @SuppressWarnings("unchecked")
    private <T> T handleEnumeration(SortedSet<PropertyProvider<?>> providers, PropertyType<T> type) {
        List<Object> list = new ArrayList<>();

        for (PropertyProvider<?> provider : providers) {
            if (shouldSkip(providers, provider)) {
                continue;
            }

            list.addAll((Collection<?>) provider.supplier().apply(this::get));
        }

        return (T) list;
    }

    @SuppressWarnings("unchecked")
    private <T> T handleMapping(SortedSet<PropertyProvider<?>> providers, PropertyType<T> type) {
        Map<Object, Object> map = new HashMap<>();

        for (PropertyProvider<?> provider : providers) {
            if (shouldSkip(providers, provider)) {
                continue;
            }

            map.putAll((Map<Object, Object>) provider.supplier().apply(this::get));
        }

        return (T) map;
    }

}
