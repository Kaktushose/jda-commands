package io.github.kaktushose.jdac.internal.eventmanager;

import io.github.kaktushose.jdac.property.internal.JDACIntrospectionImpl;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.InterfacedEventManager;

public final class InterfacedIntrospectionEventManager extends InterfacedEventManager {

    private final JDACIntrospectionImpl introspection;

    public InterfacedIntrospectionEventManager(JDACIntrospectionImpl introspection) {
        this.introspection = introspection;
    }

    @Override
    public void handle(GenericEvent event) {
        introspection.scoped().run(() -> super.handle(event));
    }
}
