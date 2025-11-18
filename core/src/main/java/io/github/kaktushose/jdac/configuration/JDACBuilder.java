package io.github.kaktushose.jdac.configuration;

import io.github.kaktushose.jdac.definitions.description.ClassFinder;

import java.util.*;
import java.util.function.Function;

public class JDACBuilder {
    private final Map<PropertyType<?>, SortedSet<PropertyProvider<?>>> properties = new HashMap<>();

    JDACBuilder() {
        addFallback(PropertyTypes.PACKAGES, _ -> List.of());

        addFallback(PropertyTypes.CLASS_FINDER, ctx -> {
            String[] resources = ctx.get(PropertyTypes.PACKAGES).toArray(String[]::new);
            return List.of(ClassFinder.reflective(resources));
        });

    }

    private <T> void addFallback(PropertyType<T> type, Function<ConfigurationContext, T> supplier) {
        properties.computeIfAbsent(type, _ -> new TreeSet<>()).add(new PropertyProvider<>(type, PropertyProvider.FALLBACK_PRIORITY, supplier));
    }

    private <T> JDACBuilder addUserProperty(PropertyType<T> type, Function<ConfigurationContext, T> supplier) {
        properties.computeIfAbsent(type, _ -> new TreeSet<>()).add(new PropertyProvider<>(type, PropertyProvider.USER_PRIORITY, supplier));
        return this;
    }

    public JDACBuilder packages(String... packages) {
        return addUserProperty(PropertyTypes.PACKAGES, _ -> List.of(packages));
    }

    public JDACBuilder classFinders(ClassFinder... classFinders) {
        return addUserProperty(PropertyTypes.CLASS_FINDER, _ -> List.of(classFinders));
    }

    static void main() {
        JDACBuilder builder = new JDACBuilder();
        builder.packages("my package");
        builder.classFinders(ClassFinder.explicit(String.class));

        Loader loader = new Loader(builder.properties);
        var value = loader.get(PropertyTypes.CLASS_FINDER);
        System.out.println(value);
    }
}
