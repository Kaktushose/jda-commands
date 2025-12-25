package io.github.kaktushose.jdac.introspection.lifecycle.events;

import io.github.kaktushose.jdac.introspection.lifecycle.Event;

public record RuntimeCloseEvent(
        String runtimeId
) implements Event {
}
