package io.github.kaktushose.jdac.dispatching.adapter;

import io.github.kaktushose.proteus.type.Type;

public record AdapterType<S, T>(Type<S> source, Type<T> target) {

}
