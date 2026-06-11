package io.github.kaktushose.jdac.property.events;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.property.JDACScope;

/// Published when a [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) is started, e.g. when the user executed a slash command.
///
/// @param runtimeId the runtimes id
@IntrospectionAccess(JDACScope.RUNTIME)
public record RuntimeCloseEvent(long runtimeId) implements JDACEvent {
}
