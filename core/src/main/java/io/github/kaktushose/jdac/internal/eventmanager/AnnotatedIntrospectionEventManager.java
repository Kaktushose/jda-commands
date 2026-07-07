package io.github.kaktushose.jdac.internal.eventmanager;

import io.github.kaktushose.jdac.property.internal.JDACIntrospectionImpl;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class AnnotatedIntrospectionEventManager extends AnnotatedEventManager {

    private final JDACIntrospectionImpl introspection;

    public AnnotatedIntrospectionEventManager(JDACIntrospectionImpl introspection) {
        this.introspection = introspection;
    }

    @Override
    public void handle(GenericEvent event) {
        introspection.scoped().run(() -> super.handle(event));
    }
}
