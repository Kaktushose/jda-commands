package io.github.kaktushose.jdac.definitions.features;

import io.github.kaktushose.jdac.definitions.Definition;

/// Indicates that the implementing [Definition] can be somehow transformed into a JDA entity.
///
/// @see CustomIdJDAEntity
/// @see NonCustomIdJDAEntity
public sealed interface JDAEntity<T> extends Definition permits CustomIdJDAEntity, NonCustomIdJDAEntity {
}
