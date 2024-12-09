package com.github.kaktushose.jda.commands.dispatching.adapter;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.refactor.context.ExecutionContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Generic top level interface for type adapting.
 *
 * @param <T> the type the adapter parses
 * @see com.github.kaktushose.jda.commands.annotations.Implementation
 * @since 2.0.0
 */
@FunctionalInterface
public interface TypeAdapter<T> extends BiFunction<String, ExecutionContext<?, ?>, Optional<T>> {

    /**
     * Attempts to parse a String to the given type.
     *
     * @param raw     the String to parse
     * @param context the {@link Context}
     * @return the parsed type or an empty Optional if the parsing fails
     */
    Optional<T> apply(@NotNull String raw, @NotNull ExecutionContext<?, ?> context);

}
