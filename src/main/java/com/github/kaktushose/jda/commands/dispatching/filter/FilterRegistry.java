package com.github.kaktushose.jda.commands.dispatching.filter;

import com.github.kaktushose.jda.commands.dispatching.filter.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FilterRegistry {

    private static final Logger log = LoggerFactory.getLogger(FilterRegistry.class);
    private final List<FilterEntry> filters;

    public FilterRegistry() {
        this.filters = new ArrayList<>();

        register(new UserMuteFilter(), FilterPosition.BEFORE_ROUTING);
        register(new PermissionsFilter(), FilterPosition.BEFORE_ADAPTING);
        register(new DirectMessageFilter(), FilterPosition.BEFORE_ADAPTING);
        register(new CooldownFilter(), FilterPosition.BEFORE_ADAPTING);
        register(new ConstraintFilter(), FilterPosition.BEFORE_EXECUTION);
    }

    public void register(Filter filter, FilterPosition position) {
        filters.add(new FilterEntry(filter, position));
        log.debug("Registered filter {}", filter.getClass().getName());
    }

    public void unregister(Class<? extends Filter> filter) {
        filters.removeIf(entry -> filter.isAssignableFrom(filter));
        log.debug("Unregistered filter(s) {}", filter.getName());
    }

    public List<Filter> getAll() {
        return Collections.unmodifiableList(filters.stream().map(entry -> entry.filter).collect(Collectors.toList()));
    }

    public List<Filter> getAll(FilterPosition position) {
        return Collections.unmodifiableList(filters.stream()
                .filter(entry -> entry.position.equals(position))
                .map(entry -> entry.filter)
                .collect(Collectors.toList()));
    }

    public enum FilterPosition {
        BEFORE_ROUTING,
        BEFORE_ADAPTING,
        BEFORE_EXECUTION
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
