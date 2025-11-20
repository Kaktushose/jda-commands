package io.github.kaktushose.jdac.configuration;

import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.function.Function;

public record PropertyProvider<T>(
        Property<T> type,
        int priority,
        Function<Context, @Nullable T> supplier
) implements Comparable<PropertyProvider<T>> {

    @Override
    public int compareTo(PropertyProvider<T> o) {
        return Comparator.<Integer>reverseOrder().compare(priority, o.priority);
    }

    public interface Context {

        <T> T get(Property<T> type);
    }
}
