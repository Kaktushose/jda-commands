package io.github.kaktushose.jdac.annotations;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.introspection.Stage;

import java.lang.annotation.*;

/// This annotation indicates that [Introspection#access()] and [Introspection#accGet(Property)] can be used
/// inside this method.
///
/// Only properties having at least the stage of [#value()] can be used.
/// - [Stage#INTERACTION] includes [Stage#CONFIGURATION], [Stage#INITIALIZED] and [Stage#RUNTIME]
/// - [Stage#RUNTIME] includes [Stage#CONFIGURATION] and [Stage#INITIALIZED]
/// - [Stage#INITIALIZED] includes [Stage#CONFIGURATION]
///
/// More technical, a [Stage] with a certain [ordinal][Stage#ordinal()] contains all stages with a lower ordinal value:
///
/// `stageA ⊆ stageB if stageA.ordinal < stageB.ordinal`
///
/// @see Stage
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface IntrospectionAccess {
    Stage value();
}
