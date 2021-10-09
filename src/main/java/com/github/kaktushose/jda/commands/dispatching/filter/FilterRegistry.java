package com.github.kaktushose.jda.commands.dispatching.filter;

import com.github.kaktushose.jda.commands.dispatching.filter.impl.ConstraintFilter;
import com.github.kaktushose.jda.commands.dispatching.filter.impl.CooldownFilter;
import com.github.kaktushose.jda.commands.dispatching.filter.impl.DirectMessageFilter;
import com.github.kaktushose.jda.commands.dispatching.filter.impl.PermissionsFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterRegistry {

    private static final Logger log = LoggerFactory.getLogger(FilterRegistry.class);
    private final List<Filter> filters;

    public FilterRegistry() {
        this.filters = new ArrayList<>();

        register(new PermissionsFilter());
        register(new DirectMessageFilter());
        register(new CooldownFilter());
        register(new ConstraintFilter());
    }

    public void register(Filter filter) {
        filters.add(filter);
        log.debug("Registered filter {}", filter.getClass().getName());
    }

    public void unregister(Filter filter) {
        filters.remove(filter);
        log.debug("Unregistered filter {}", filter.getClass().getName());
    }

    public List<Filter> getAll() {
        return Collections.unmodifiableList(filters);
    }
}
