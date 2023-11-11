package com.github.kaktushose.jda.commands.dispatching.middleware;

import com.github.kaktushose.jda.commands.dispatching.middleware.impl.ConstraintMiddleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.CooldownMiddleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.PermissionsMiddleware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class MiddlewareRegistry {

    private static final Logger log = LoggerFactory.getLogger(MiddlewareRegistry.class);
    private final Map<Priority, Set<Middleware>> middlewares;

    public MiddlewareRegistry() {
        middlewares = new HashMap<>();
        middlewares.put(Priority.LOW, new HashSet<>());
        middlewares.put(Priority.NORMAL, new HashSet<>());
        middlewares.put(Priority.HIGH, new HashSet<>());
        middlewares.put(Priority.PERMISSIONS, new HashSet<>());
        register(Priority.PERMISSIONS, new PermissionsMiddleware());
        register(Priority.NORMAL, new ConstraintMiddleware(), new CooldownMiddleware());
    }

    public MiddlewareRegistry register(Priority priority, Middleware... middlewares) {
        register(priority, List.of(middlewares));
        return this;
    }

    public MiddlewareRegistry register(Priority priority, Collection<Middleware> middlewares) {
        this.middlewares.get(priority).addAll(middlewares);
        log.debug("Registered middleware(s) {} with priority {}", middlewares, priority);
        return this;
    }

    public MiddlewareRegistry unregister(Priority priority, Middleware... middlewares) {
        unregister(priority, List.of(middlewares));
        return this;
    }

    public MiddlewareRegistry unregister(Priority priority, Collection<Middleware> middlewares) {
        this.middlewares.get(priority).removeAll(middlewares);
        log.debug("Unregistered middleware(s) {}", middlewares);
        return this;
    }

    public Set<Middleware> getMiddlewares() {
        return middlewares.values().stream().flatMap(Collection::stream).collect(Collectors.toUnmodifiableSet());
    }

    public Set<Middleware> getMiddlewares(Priority priority) {
        return Collections.unmodifiableSet(middlewares.get(priority));
    }

}
