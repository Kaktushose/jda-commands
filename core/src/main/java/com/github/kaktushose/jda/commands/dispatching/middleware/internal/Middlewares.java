package com.github.kaktushose.jda.commands.dispatching.middleware.internal;

import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.ConstraintMiddleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.CooldownMiddleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.PermissionsMiddleware;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/// Central registry for all [Middleware]s.
public class Middlewares {

    private final SortedMap<Priority, Set<Middleware>> middlewares;

    public Middlewares(@NotNull Collection<Map.Entry<Priority, Middleware>> userDefined, @NotNull ErrorMessageFactory errorMessageFactory, @NotNull PermissionsProvider permissionsProvider) {
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

    /// Returns a set of all registered [Middleware]s, regardless of their priority.
    ///
    /// @return a set of all registered middlewares [Middleware]s
    @NotNull
    public Set<Middleware> getMiddlewares() {
        return middlewares.sequencedValues()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

    /// Executed the given task for all registered [Middleware]s ordered by their [Priority]
    /// @param task The task to be executed
    public void forAllOrdered(@NotNull Consumer<Middleware> task) {
        for (Set<Middleware> value : middlewares.values()) {
            value.forEach(task);
        }
    }

}
