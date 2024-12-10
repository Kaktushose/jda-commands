package com.github.kaktushose.jda.commands.dispatching.middleware;

import com.github.kaktushose.jda.commands.dispatching.middleware.impl.ConstraintMiddleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.CooldownMiddleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.PermissionsMiddleware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Central registry for all {@link Middleware Middlewares}.
 *
 * @since 4.0.0
 */
public class MiddlewareRegistry {

    private static final Logger log = LoggerFactory.getLogger(MiddlewareRegistry.class);
    private final SortedMap<Priority, Set<Middleware>> middlewares;

    /**
     * Constructs a new MiddlewareRegistry.
     */
    public MiddlewareRegistry() {
        middlewares = new TreeMap<>();
        middlewares.put(Priority.LOW, new HashSet<>());
        middlewares.put(Priority.NORMAL, new HashSet<>());
        middlewares.put(Priority.HIGH, new HashSet<>());
        middlewares.put(Priority.PERMISSIONS, new HashSet<>());
        register(Priority.PERMISSIONS, new PermissionsMiddleware());
        register(Priority.NORMAL, new ConstraintMiddleware(), new CooldownMiddleware());
    }

    /**
     * Register {@link Middleware Middleware(s)} with the given {@link Priority}.
     *
     * @param priority    the {@link Priority} to register the {@link Middleware Middleware(s)} with
     * @param first       the first {@link Middleware} to register
     * @param middlewares additional {@link Middleware Middlewares} to register
     * @return this instance for fluent interface
     */
    public MiddlewareRegistry register(Priority priority, Middleware first, Middleware... middlewares) {
        register(priority, Stream.concat(Stream.of(first), Arrays.stream(middlewares)).collect(Collectors.toList()));
        return this;
    }

    /**
     * Register {@link Middleware Middleware(s)} with the given {@link Priority}.
     *
     * @param priority    the {@link Priority} to register the {@link Middleware Middleware(s)} with
     * @param middlewares the {@link Middleware Middleware(s)} to register
     * @return this instance for fluent interface
     */
    public MiddlewareRegistry register(Priority priority, Collection<Middleware> middlewares) {
        this.middlewares.get(priority).addAll(middlewares);
        log.debug("Registered middleware(s) {} with priority {}", middlewares, priority);
        return this;
    }

    /**
     * Unregister {@link Middleware Middleware(s)} with the given {@link Priority}.
     *
     * @param priority    the {@link Priority} to unregister the {@link Middleware Middleware(s)} with
     * @param first       the first {@link Middleware} to unregister
     * @param middlewares additional {@link Middleware Middlewares} to unregister
     * @return this instance for fluent interface
     */
    public MiddlewareRegistry unregister(Priority priority, Middleware first, Middleware... middlewares) {
        unregister(priority, Stream.concat(Stream.of(first), Arrays.stream(middlewares)).collect(Collectors.toList()));
        return this;
    }

    /**
     * Unregister {@link Middleware Middleware(s)} with the given {@link Priority}.
     *
     * @param priority    the {@link Priority} to unregister the {@link Middleware Middleware(s)} with
     * @param middlewares the {@link Middleware Middleware(s)} to unregister
     * @return this instance for fluent interface
     */
    public MiddlewareRegistry unregister(Priority priority, Collection<Middleware> middlewares) {
        this.middlewares.get(priority).removeAll(middlewares);
        log.debug("Unregistered middleware(s) {}", middlewares);
        return this;
    }

    /**
     * Returns a set of all registered {@link Middleware Middlewares}, regardless of their priority.
     *
     * @return a set of all registered middlewares {@link Middleware Middlewares}
     */
    public Set<Middleware> getMiddlewares() {
        return middlewares.sequencedValues().stream().flatMap(Collection::stream).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns a possibly-empty set of all registered {@link Middleware Middlewares} with the given {@link Priority}.
     *
     * @return a set of all registered middlewares {@link Middleware Middlewares} with the given {@link Priority}
     */
    public Set<Middleware> getMiddlewares(Priority priority) {
        return Collections.unmodifiableSet(middlewares.get(priority));
    }

}
