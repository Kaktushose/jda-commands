package io.github.kaktushose.jdac.property.internal;

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
        introspection.scoped().run(() -> delegate.handle(event));
    }

    @Override
    public List<Object> getRegisteredListeners() {
        return delegate.getRegisteredListeners();
    }
}
