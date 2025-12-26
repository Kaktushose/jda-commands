package io.github.kaktushose.jdac.introspection.lifecycle.events;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.introspection.lifecycle.FrameworkEvent;

/// Published when a [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) is closed, e.g. due to inactivity.
///
/// @param id the runtimes id
@IntrospectionAccess(Stage.RUNTIME)
public record RuntimeOpenEvent(String id) implements FrameworkEvent {
}
