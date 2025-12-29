package io.github.kaktushose.jdac.definitions.features;

import io.github.kaktushose.jdac.definitions.Definition;

public sealed interface JDAEntity<T> extends Definition permits CustomIdJDAEntity, NonCustomIdJDAEntity {
}
