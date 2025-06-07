package com.github.kaktushose.jda.commands.dispatching.adapter;

import io.github.kaktushose.proteus.mapping.Mapper.UniMapper;

/// Generic top level interface for type adapting.
///
/// @param <S> the source type
/// @param <T> the target type
@FunctionalInterface
public interface TypeAdapter<S, T> extends UniMapper<S, T> {

}
