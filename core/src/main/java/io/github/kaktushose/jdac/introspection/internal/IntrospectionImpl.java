package io.github.kaktushose.jdac.introspection.internal;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.internal.Properties;
import io.github.kaktushose.jdac.configuration.internal.Resolver;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.introspection.lifecycle.FrameworkEvent;
import io.github.kaktushose.jdac.introspection.lifecycle.Subscriber;
import io.github.kaktushose.jdac.introspection.lifecycle.Subscription;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class IntrospectionImpl implements Introspection {

    public static final ScopedValue<IntrospectionImpl> INTROSPECTION = ScopedValue.newInstance();

    private final Lifecycle lifecycle;
    private final Resolver resolver;
    private final Stage stage;

    public IntrospectionImpl(Lifecycle lifecycle, Resolver resolver, Stage stage) {
        Properties introspectionProperty = Properties.Builder.newRestricted()
                .addFallback(Property.INTROSPECTION, _ -> this)
                .build();

        this.lifecycle = lifecycle;
        this.resolver = resolver.createSub(introspectionProperty);;
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

    @Override
    public <T extends FrameworkEvent> Subscription subscribe(Class<T> event, Subscriber<T> subscriber) {
        return lifecycle.subscribe(event, subscriber);
    }

    public void publish(FrameworkEvent event) {
        lifecycle.publish(event, this);
    }

    public IntrospectionImpl createSub(Properties properties, Stage stage) {
        return new IntrospectionImpl(
                this.lifecycle,
                resolver.createSub(properties),
                stage
        );
    }
}
