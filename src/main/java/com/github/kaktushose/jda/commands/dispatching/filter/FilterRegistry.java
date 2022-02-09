package com.github.kaktushose.jda.commands.dispatching.filter;

import com.github.kaktushose.jda.commands.dispatching.filter.impl.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private final List<FilterEntry> filters;

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
        this.filters = new ArrayList<>();

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
        filters.add(new FilterEntry(filter, position));
        log.debug("Registered filter {} for position {}", filter.getClass().getName(), position);
    }

    /**
     * Unregisters all occurrences of the given {@link Filter} regardless of the {@link FilterPosition FilterPosition}.
     *
     * @param filter the {@link Filter} to unregister
     */
    public void unregister(@NotNull Class<? extends Filter> filter) {
        filters.removeIf(entry -> filter.isAssignableFrom(filter));
        log.debug("Unregistered filter(s) {}", filter.getName());
    }

    /**
     * Retrieves all available {@link Filter Filters} regardless of their {@link FilterPosition FilterPosition}. This
     * List might contain duplicates.
     *
     * @return all registered {@link Filter Filters}
     */
    public List<Filter> getAll() {
        return Collections.unmodifiableList(filters.stream().map(entry -> entry.filter).collect(Collectors.toList()));
    }

    /**
     * Retrieves all {@link Filter Filters} that are registered for the given {@link FilterPosition FilterPosition}.
     *
     * @param position the {@link FilterPosition} to retrieve the {@link Filter Filters} for
     * @return all registered {@link Filter Filters}
     */
    public List<Filter> getAll(@NotNull FilterPosition position) {
        return Collections.unmodifiableList(filters.stream()
                .filter(entry -> entry.position.equals(position))
                .map(entry -> entry.filter)
                .collect(Collectors.toList()));
    }

    /**
     * Enum describing different filter positions.
     *
     * @author Kaktushose
     * @version 2.2.0
     * @see com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry
     * @since 2.0.0
     */
    public enum FilterPosition {

        /**
         * Filter will be executed before command routing. The command will not be present in the
         * {@link com.github.kaktushose.jda.commands.dispatching.CommandContext}.
         */
        BEFORE_ROUTING,

        /**
         * Filter will be executed before type adapting. The command will be present in the
         * {@link com.github.kaktushose.jda.commands.dispatching.CommandContext} but not the type adapted input.
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

    private static class FilterEntry {

        private final Filter filter;
        private final FilterPosition position;

        public FilterEntry(Filter filter, FilterPosition position) {
            this.filter = filter;
            this.position = position;
        }
    }

}
