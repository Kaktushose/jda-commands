package io.github.kaktushose.jdac.configuration;

public interface ConfigurationContext {
    <T> T get(PropertyType<T> type);
}
