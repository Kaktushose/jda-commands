package io.github.kaktushose.jdac.introspection.lifecycle;

import io.github.kaktushose.jdac.introspection.Introspection;

import java.util.function.BiConsumer;

public interface Subscriber<T extends Event> extends BiConsumer<T, Introspection> {
}
