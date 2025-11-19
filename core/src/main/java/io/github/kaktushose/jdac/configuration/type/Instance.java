package io.github.kaktushose.jdac.configuration.type;

import io.github.kaktushose.jdac.configuration.PropertyType;

public record Instance<T>(String name, PropertyType.Scope scope, Class<T> type) implements PropertyType<T> {
    @Override
    public FallbackBehaviour fallbackBehaviour() {
        throw new UnsupportedOperationException("fallback behaviour not supported on PropertyType.Instance");
    }
}
