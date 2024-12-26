package com.github.kaktushose.jda.commands.dispatching.middleware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// Central registry for all [Middlewares][Middleware].
public class MiddlewareRegistry {

    private static final Logger log = LoggerFactory.getLogger(MiddlewareRegistry.class);
    private final SortedMap<Priority, Set<Middleware>> middlewares = new TreeMap<>(Map.of(
            Priority.PERMISSIONS, new HashSet<>(),
            Priority.HIGH, new HashSet<>(),
            Priority.NORMAL, new HashSet<>(),
            Priority.LOW, new HashSet<>()
    ));

    /// Register [Middleware(s)][Middleware] with the given [Priority].
    ///
    /// @param priority    the [Priority] to register the [Middleware(s)][Middleware] with
    /// @param first       the first [Middleware] to register
    /// @param middlewares additional [Middlewares][Middleware] to register
    /// @return this instance for fluent interface
    public MiddlewareRegistry register(Priority priority, Middleware first, Middleware... middlewares) {
        register(priority, Stream.concat(Stream.of(first), Arrays.stream(middlewares)).collect(Collectors.toList()));
        return this;
    }

    /// Register [Middleware(s)][Middleware] with the given [Priority].
    ///
    /// @param priority    the [Priority] to register the [Middleware(s)][Middleware] with
    /// @param middlewares the [Middleware(s)][Middleware] to register
    /// @return this instance for fluent interface
    public MiddlewareRegistry register(Priority priority, Collection<Middleware> middlewares) {
        this.middlewares.get(priority).addAll(middlewares);
        log.debug("Registered middleware(s) {} with priority {}", middlewares, priority);
        return this;
    }

    /// Unregister [Middleware(s)][Middleware] with the given [Priority].
    ///
    /// @param priority    the [Priority] to unregister the [Middleware(s)][Middleware] with
    /// @param first       the first [Middleware] to unregister
    /// @param middlewares additional [Middlewares][Middleware] to unregister
    /// @return this instance for fluent interface
    public MiddlewareRegistry unregister(Priority priority, Middleware first, Middleware... middlewares) {
        unregister(priority, Stream.concat(Stream.of(first), Arrays.stream(middlewares)).collect(Collectors.toList()));
        return this;
    }

    /// Unregister [Middleware(s)][Middleware] with the given [Priority].
    ///
    /// @param priority    the [Priority] to unregister the [Middleware(s)][Middleware] with
    /// @param middlewares the [Middleware(s)][Middleware] to unregister
    /// @return this instance for fluent interface
    public MiddlewareRegistry unregister(Priority priority, Collection<Middleware> middlewares) {
        this.middlewares.get(priority).removeAll(middlewares);
        log.debug("Unregistered middleware(s) {}", middlewares);
        return this;
    }

    /// Returns a set of all registered [Middlewares][Middleware], regardless of their priority.
    ///
    /// @return a set of all registered middlewares [Middlewares][Middleware]
    public Set<Middleware> getMiddlewares() {
        return middlewares.sequencedValues()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

    public void forAllOrdered(Consumer<Middleware> task) {
        for (Set<Middleware> value : middlewares.values()) {
            value.forEach(task);
        }
    }

    /// Returns a possibly-empty set of all registered [Middlewares][Middleware] with the given [Priority].
    ///
    /// @return a set of all registered middlewares [Middlewares][Middleware] with the given [Priority]
    public Set<Middleware> getMiddlewares(Priority priority) {
        return Collections.unmodifiableSet(middlewares.get(priority));
    }

}
