package io.github.kaktushose.jdac.introspection.internal;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.internal.Properties;
import io.github.kaktushose.jdac.configuration.internal.Resolver;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.introspection.Stage;

public final class IntrospectionImpl implements Introspection {

    private final Resolver resolver;
    private final Stage stage;

    public IntrospectionImpl(Resolver resolver, Stage stage) {
        this.resolver = resolver;
        this.stage = stage;
    }

    @Override
    public Stage currentStage() {
        return stage;
    }

    @Override
    public <T> T get(Property<T> type) {
        if (type.stage().ordinal() > currentStage().ordinal()) {
            throw new IllegalArgumentException("TODO add exception: property of stage %s not available in stage %s".formatted(type.stage(), currentStage()));
        }

        return resolver.get(type);
    }

    public IntrospectionImpl createSub(Properties properties, Stage stage) {
        return new IntrospectionImpl(
                resolver.createSub(properties),
                stage
        );
    }

}
