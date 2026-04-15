package io.github.kaktushose.jdac.dispatching.adapter;

import io.github.kaktushose.jdac.annotations.IntrospectionAccess;
import io.github.kaktushose.jdac.property.JDACScope;
import io.github.kaktushose.proteus.mapping.Mapper.UniMapper;
import io.github.kaktushose.proteus.mapping.MappingResult;

/// Generic top level interface for type adapting.
///
/// @param <S> the source type
/// @param <T> the target type
@FunctionalInterface
public interface TypeAdapter<S, T> extends UniMapper<S, T> {

    /// {@inheritDoc}
    @IntrospectionAccess(JDACScope.PREPARATION)
    MappingResult<T> from(S source, MappingContext<S, T> context);
}
