package io.github.kaktushose.jdac.property.internal;

import io.github.kaktushose.jdac.property.JDACScope;
import io.github.kaktushose.jdac.property.events.*;

import java.util.Map;

import static java.util.Map.entry;

public final class Registry extends dev.goldmensch.propane.Registry<JDACScope> {
    public static final Registry INSTANCE = new Registry();

    Registry() {
        super(Map.ofEntries(
                entry(FrameworkShutdownEvent.class, JDACScope.INITIALIZED),
                entry(FrameworkStartEvent.class, JDACScope.INITIALIZED),
                entry(InteractionFinishedEvent.class, JDACScope.INTERACTION),
                entry(InteractionStartEvent.class, JDACScope.INTERACTION),
                entry(RuntimeCloseEvent.class, JDACScope.RUNTIME),
                entry(RuntimeOpenEvent.class, JDACScope.RUNTIME)
        ));
    }
}
