package com.github.kaktushose.jda.commands.dispatching.filter;

import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.filter.impl.ConstraintFilter;
import com.github.kaktushose.jda.commands.dispatching.filter.impl.CooldownFilter;
import com.github.kaktushose.jda.commands.dispatching.filter.impl.DirectMessageFilter;
import com.github.kaktushose.jda.commands.dispatching.filter.impl.PermissionsFilter;
import com.github.kaktushose.jda.commands.dispatching.filter.impl.UserMuteFilter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Central registry for all {@link Filter Filters}.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see Filter
 * @since 2.0.0
 */
public class FilterRegistry {

    private static final Logger log = LoggerFactory.getLogger(FilterRegistry.class);
    private final Map<FilterPosition, Set<Filter>> filters;

    /**
     * Constructs a new FilterRegistry. This will register the following {@link Filter Filters} by default:
     * <ul>
     *     <li>{@link ConstraintFilter}</li>
     *     <li>{@link CooldownFilter}</li>
     *     <li>{@link DirectMessageFilter}</li>
     *     <li>{@link PermissionsFilter}</li>
     *     <li>{@link UserMuteFilter}</li>
     * </ul>
     */
    public FilterRegistry() {
        this.filters = new EnumMap<>(FilterPosition.class);

        register(new UserMuteFilter(), FilterPosition.BEFORE_ROUTING);
        register(new PermissionsFilter(), FilterPosition.BEFORE_ADAPTING);
        register(new DirectMessageFilter(), FilterPosition.BEFORE_ADAPTING);
        register(new CooldownFilter(), FilterPosition.BEFORE_ADAPTING);
        register(new ConstraintFilter(), FilterPosition.BEFORE_EXECUTION);
    }

    /**
     * Register a {@link Filter} for a specific {@link FilterPosition FilterPosition}. One {@link Filter} can be
     * registered multiple times, even for the same {@link FilterPosition FilterPosition}.
     *
     * @param filter   the {@link Filter} to register
     * @param position the {@link FilterPosition FilterPosition} at which the {@link Filter} gets registered
     */
    public void register(@NotNull Filter filter, @NotNull FilterPosition position) {
        filters.putIfAbsent(position, new HashSet<>());
        filters.get(position).add(filter);
        log.debug("Registered filter {} for position {}", filter.getClass().getName(), position);
    }

    /**
     * Unregisters all occurrences of the given {@link Filter} regardless of the {@link FilterPosition FilterPosition}.
     *
     * @param filter the {@link Filter} to unregister
     */
    public void unregister(@NotNull Class<? extends Filter> filter) {
        filters.keySet().forEach(position -> unregister(filter, position));
        log.debug("Unregistered filter(s) {}", filter.getName());
    }

    /**
     * Unregisters all occurrences of the given {@link Filter} with the given {@link FilterPosition}.
     *
     * @param filter   the {@link Filter} to unregister
     * @param position the {@link FilterPosition} to use
     */
    public void unregister(Class<? extends Filter> filter, FilterPosition position) {
        Set<Filter> filterSet = filters.get(position);
        if (filterSet != null) {
            filterSet.removeIf(current -> current.getClass().isAssignableFrom(filter));
        }
    }

    /**
     * Retrieves all available {@link Filter Filters} regardless of their {@link FilterPosition FilterPosition}.
     *
     * @return all registered {@link Filter Filters}
     */
    public Collection<Filter> getAll() {
        return filters.values()
                .stream()
                .flatMap(Set::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Retrieves all {@link Filter Filters} that are registered for the given {@link FilterPosition FilterPosition}.
     *
     * @param position the {@link FilterPosition} to retrieve the {@link Filter Filters} for
     * @return all registered {@link Filter Filters}
     */
    public Collection<Filter> getAll(@NotNull FilterPosition position) {
        return Collections.unmodifiableCollection(filters.get(position));
    }

    /**
     * Enum describing different filter positions.
     *
     * @author Kaktushose
     * @version 2.2.0
     * @see TypeAdapterRegistry
     * @since 2.0.0
     */
    public enum FilterPosition {

        /**
         * Filter will be executed before command routing. The command will not be present in the
         * {@link GenericContext}.
         */
        BEFORE_ROUTING,

        /**
         * Filter will be executed before type adapting. The command will be present in the
         * {@link GenericContext} but not the type adapted input.
         */
        BEFORE_ADAPTING,

        /**
         * Filter will be executed just before the command execution.
         */
        BEFORE_EXECUTION,

        /**
         * Filter will not be executed.
         */
        UNKNOWN
    }
}
