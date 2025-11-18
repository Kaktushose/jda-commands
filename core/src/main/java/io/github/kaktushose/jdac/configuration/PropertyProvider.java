package io.github.kaktushose.jdac.configuration;

import java.util.function.Function;

public record PropertyProvider<T>(
        PropertyType<T> type,
        int priority,
        Function<ConfigurationContext, T> supplier
) implements Comparable<PropertyProvider<T>> {

    @Override
    public int compareTo(PropertyProvider<T> o) {
        return Integer.compare(priority(), o.priority());
    }
}
