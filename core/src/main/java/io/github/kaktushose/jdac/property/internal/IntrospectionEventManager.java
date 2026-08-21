package io.github.kaktushose.jdac.property.internal;

import io.github.kaktushose.jdac.property.JDACProperty;
import io.github.kaktushose.jdac.property.JDACScope;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public final class IntrospectionEventManager implements IEventManager {

    private final IEventManager delegate;
    private final JDACIntrospectionImpl introspection;

    public IntrospectionEventManager(IEventManager delegate, JDACIntrospectionImpl introspection) {
        this.delegate = delegate;
        this.introspection = introspection;
    }

    @Override
    public void register(Object listener) {
        delegate.register(listener);
    }

    @Override
    public void unregister(Object listener) {
        delegate.unregister(listener);
    }

    @Override
    public void handle(GenericEvent event) {
        introspection.createChild(JDACScope.GENERIC_EVENT)
                .addFallback(JDACProperty.GENERIC_EVENT, _ -> event)
                .build()
                .scoped()
                .run(() -> delegate.handle(event));
    }

    @Override
    public List<Object> getRegisteredListeners() {
        return delegate.getRegisteredListeners();
    }
}
