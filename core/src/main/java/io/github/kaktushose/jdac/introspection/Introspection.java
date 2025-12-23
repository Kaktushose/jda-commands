package io.github.kaktushose.jdac.introspection;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.internal.Resolver;
import io.github.kaktushose.jdac.dispatching.handling.EventHandler;

public class Introspection {

    private final Resolver resolver;
    private final Stage stage;

    public Introspection(Resolver resolver, Stage stage) {
        this.resolver = resolver;
        this.stage = stage;
    }

    public static boolean accessible() {
        return EventHandler.INTROSPECTION.isBound();
    }

    public static Introspection access() {
        return EventHandler.INTROSPECTION.get();
    }

    public static <T> T accGet(Property<T> type) {
        return access().get(type);
    }

    public Stage currentStage() {
        return stage;
    }

    public <T> T get(Property<T> type) {
        if (type.stage().ordinal() > currentStage().ordinal()) {
            throw new IllegalArgumentException("TODO add exception: property of stage %s not available in stage %s".formatted(type.stage(), currentStage()));
        }

        return resolver.get(type);
    }

}
