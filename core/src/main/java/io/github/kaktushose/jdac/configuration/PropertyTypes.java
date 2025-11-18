package io.github.kaktushose.jdac.configuration;

import io.github.kaktushose.jdac.definitions.description.ClassFinder;

import java.util.Collection;

import static io.github.kaktushose.jdac.configuration.PropertyType.ValueType.*;

public class PropertyTypes {
    public static final PropertyType<Collection<String>> PACKAGES =
            new PropertyType<>(new Enumeration<>(String.class), PropertyType.FallbackBehaviour.ACCUMULATE);

    public static final PropertyType<Collection<ClassFinder>> CLASS_FINDER =
            new PropertyType<>(new Enumeration<>(ClassFinder.class), PropertyType.FallbackBehaviour.OVERRIDE);
}
