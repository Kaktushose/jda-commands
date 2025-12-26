package io.github.kaktushose.jdac.introspection.lifecycle.events;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.introspection.Stage;
import io.github.kaktushose.jdac.introspection.lifecycle.FrameworkEvent;

/// Published when a [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) is started, e.g. when the user executed a slash command.
///
/// @param runtimeId the runtimes id
@IntrospectionAccess(Stage.RUNTIME)
public record RuntimeCloseEvent(String runtimeId) implements FrameworkEvent {
}
