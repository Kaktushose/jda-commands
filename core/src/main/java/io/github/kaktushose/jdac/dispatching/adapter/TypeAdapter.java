package io.github.kaktushose.jdac.dispatching.adapter;

import io.github.kaktushose.proteus.mapping.Mapper.UniMapper;
import io.github.kaktushose.proteus.mapping.MappingResult;
import org.jetbrains.annotations.NotNull;

/// Generic top level interface for type adapting.
///
/// @param <S> the source type
/// @param <T> the target type
@FunctionalInterface
public interface TypeAdapter<S, T> extends UniMapper<S, T> {
    
    /// {@inheritDoc}
    MappingResult<T> from(@NotNull S source, MappingContext<S, T> context);
}
