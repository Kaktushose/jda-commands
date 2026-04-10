package io.github.kaktushose.jdac.annotations;

import io.github.kaktushose.jdac.property.JDACIntrospection;
import io.github.kaktushose.jdac.property.JDACProperty;
import io.github.kaktushose.jdac.property.JDACScope;
import io.github.kaktushose.jdac.property.events.RuntimeCloseEvent;

import java.lang.annotation.*;

/// This annotation indicates that [JDACIntrospection#accessScoped()] and [JDACIntrospection#scopedGet(JDACProperty)] can be used
/// inside the method annotated with this annotation.
///
/// If this annotation is present on a Class like [RuntimeCloseEvent] it's up to the use case which meaning this has, please
/// read the Javadocs there.
///
/// Only properties having at least the stage of [#value()] can be used.
/// - [JDACScope#INTERACTION] includes [JDACScope#CONFIGURATION], [JDACScope#INITIALIZED], [JDACScope#RUNTIME] and [JDACScope#PREPARATION]
/// - [JDACScope#PREPARATION] includes [JDACScope#CONFIGURATION], [JDACScope#INITIALIZED] and [JDACScope#RUNTIME]
/// - [JDACScope#RUNTIME] includes [JDACScope#CONFIGURATION] and [JDACScope#INITIALIZED]
/// - [JDACScope#INITIALIZED] includes [JDACScope#CONFIGURATION]
///
/// More technical, a [JDACScope] with a certain [ordinal][JDACScope#ordinal()] contains all stages with a lower ordinal value:
///
/// `stageA ⊆ stageB if stageA.ordinal < stageB.ordinal`
///
/// @see JDACScope
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface IntrospectionAccess {

    /// The [JDACScope] of the available [JDACIntrospection] instance.
    JDACScope value();
}
