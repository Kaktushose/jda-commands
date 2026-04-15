package io.github.kaktushose.jdac.property.events;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.property.JDACIntrospection;
import io.github.kaktushose.jdac.property.JDACScope;
import io.github.kaktushose.jdac.property.internal.Registry;
import dev.goldmensch.propane.IntrospectionSkeleton;
import dev.goldmensch.propane.event.Event;
import dev.goldmensch.propane.event.Listener;

/// An Event in general, is an occurrence of something during the runtime of JDA-Commands.
/// For example, this can be [the framework start][FrameworkStartEvent] or an [interaction start][RuntimeOpenEvent].
///
/// In JDA-Commands each [JDACEvent] is bound to the [JDACScope] in which it occurs.
///
/// The [IntrospectionAccess] annotation present on implementors of this interface like
/// [FrameworkStartEvent] indicate, which scope the [JDACIntrospection] passed to
/// [Listener#accept(Event, IntrospectionSkeleton)] has.
///
/// @see Listener
public interface JDACEvent extends Event<JDACScope> {
    default JDACScope scope() {
        return Registry.INSTANCE.scopeForEvent(this.getClass());
    }
}
