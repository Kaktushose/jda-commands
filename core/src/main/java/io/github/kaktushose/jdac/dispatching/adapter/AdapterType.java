package io.github.kaktushose.jdac.dispatching.adapter;

import io.github.kaktushose.proteus.type.Type;

/// Defines the source [Type] that will be converted to the target [Type].
///
/// @param source the source [Type] that will be converted to the target [Type]
/// @param target the target [Type] that will be converted from the source [Type]
public record AdapterType<S, T>(Type<S> source, Type<T> target) {
}
