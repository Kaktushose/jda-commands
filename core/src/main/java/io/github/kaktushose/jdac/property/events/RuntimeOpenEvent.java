package io.github.kaktushose.jdac.property.events;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.property.JDACScope;
import io.github.kaktushose.jdac.property.internal.JDACEvent;

/// Published when a [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) is closed, e.g. due to inactivity.
///
/// @param id the runtimes id
@IntrospectionAccess(JDACScope.RUNTIME)
public record RuntimeOpenEvent(String id) implements JDACEvent {
}
