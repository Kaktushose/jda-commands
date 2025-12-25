package io.github.kaktushose.jdac.introspection.lifecycle;

import io.github.kaktushose.jdac.introspection.lifecycle.events.RuntimeCloseEvent;

public sealed interface Event permits RuntimeCloseEvent {

}
