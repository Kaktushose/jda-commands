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

/// Central registry for all [Middleware]s.
public class Middlewares {

    private final SortedMap<Priority, Set<Middleware>> middlewares;

    public Middlewares(Collection<Map.Entry<Priority, Middleware>> userDefined, ErrorMessageFactory errorMessageFactory, PermissionsProvider permissionsProvider) {
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

    /// Executed the given task for all registered [Middleware]s ordered by their [Priority]
    ///
    /// @param task The task to be executed
    public void forAllOrdered(Consumer<Middleware> task) {
        for (Set<Middleware> value : middlewares.values()) {
            value.forEach(task);
        }
    }

    /// Executed the given task registered [Middleware]s that should run for this interaction controller
    /// ordered by their [Priority].
    ///
    /// @param controllerClass the interaction controllers class
    /// @param task The task to be executed
    public void forOrdered(Class<?> controllerClass, Consumer<Middleware> task) {
        forAllOrdered(middleware -> {
            if (middleware.runFor() == null || middleware.runFor().contains(controllerClass)) {
                task.accept(middleware);
            }
        });
    }

}
