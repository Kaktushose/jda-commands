package io.github.kaktushose.jdac.dispatching.adapter;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.introspection.Stage;
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
    @IntrospectionAccess(Stage.RUNTIME)
    MappingResult<T> from(@NotNull S source, @NotNull MappingContext<S, T> context);
}
