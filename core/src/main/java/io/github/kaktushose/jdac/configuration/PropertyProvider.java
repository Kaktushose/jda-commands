package io.github.kaktushose.jdac.configuration;

import java.util.function.Function;

public record PropertyProvider<T>(
        PropertyType<T> type,
        int priority,
        Function<ConfigurationContext, T> supplier
) implements Comparable<PropertyProvider<T>> {

    public static final int FALLBACK_PRIORITY = 0;
    public static final int USER_PRIORITY = Integer.MAX_VALUE;

    @Override
    public int compareTo(PropertyProvider<T> o) {
        return Integer.compare(priority(), o.priority());
    }
}
