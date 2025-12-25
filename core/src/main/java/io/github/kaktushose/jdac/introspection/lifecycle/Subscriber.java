package io.github.kaktushose.jdac.introspection.lifecycle;

import java.util.function.Consumer;

public interface Subscriber<T extends Event> extends Consumer<T> {
}
