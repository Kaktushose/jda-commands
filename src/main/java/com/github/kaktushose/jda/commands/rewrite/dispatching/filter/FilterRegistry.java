package com.github.kaktushose.jda.commands.rewrite.dispatching.filter;

import com.github.kaktushose.jda.commands.rewrite.dispatching.filter.impl.ConstraintFilter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.filter.impl.CooldownFilter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.filter.impl.DirectMessageFilter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.filter.impl.PermissionsFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterRegistry {

    private final List<Filter> filters;

    public FilterRegistry() {
        this.filters = new ArrayList<>();

        register(new DirectMessageFilter());
        register(new PermissionsFilter());
        register(new CooldownFilter());
        register(new ConstraintFilter());
    }

    // TODO this is the place to add positioning
    public void register(Filter filter) {
        filters.add(filter);
    }

    public void unregister(Filter filter) {
        filters.remove(filter);
    }

    public List<Filter> getAll() {
        return Collections.unmodifiableList(filters);
    }

}
