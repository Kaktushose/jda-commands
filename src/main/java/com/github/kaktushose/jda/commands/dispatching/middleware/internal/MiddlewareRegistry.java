package com.github.kaktushose.jda.commands.dispatching.middleware.internal;

import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.ConstraintMiddleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.CooldownMiddleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.PermissionsMiddleware;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/// Central registry for all [Middleware]s.
public class MiddlewareRegistry {

    private final SortedMap<Priority, Set<Middleware>> middlewares;

    public MiddlewareRegistry(Set<Map.Entry<Priority, Middleware>> userDefined, ErrorMessageFactory errorMessageFactory, PermissionsProvider permissionsProvider) {
        SortedMap<Priority, Set<Middleware>> middlewareMap = new TreeMap<>(Map.of(
                Priority.PERMISSIONS, new HashSet<>(List.of(new PermissionsMiddleware(permissionsProvider, errorMessageFactory))),
                Priority.HIGH, new HashSet<>(),
                Priority.NORMAL, new HashSet<>(List.of(new ConstraintMiddleware(errorMessageFactory), new CooldownMiddleware(errorMessageFactory))),
                Priority.LOW, new HashSet<>()
        ));

        userDefined.forEach(entry -> middlewareMap.get(entry.getKey()).add(entry.getValue()));

        middlewareMap.computeIfPresent(Priority.PERMISSIONS, (_, set) -> Collections.unmodifiableSet(set));
        middlewareMap.computeIfPresent(Priority.HIGH, (_, set) -> Collections.unmodifiableSet(set));
        middlewareMap.computeIfPresent(Priority.NORMAL, (_, set) -> Collections.unmodifiableSet(set));
        middlewareMap.computeIfPresent(Priority.LOW, (_, set) -> Collections.unmodifiableSet(set));

        this.middlewares = Collections.unmodifiableSortedMap(middlewareMap);
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

}
