package io.github.kaktushose.jdac.components.pagination.internal;

import io.github.kaktushose.jdac.components.pagination.Pagination;
import io.github.kaktushose.jdac.components.pagination.PaginationLayout;
import org.jspecify.annotations.Nullable;

import java.util.SequencedCollection;

public record PaginationImpl(
        SequencedCollection<PaginationLayout> paginationLayouts,
        ContainerConfig config
) implements Pagination {

    public PaginationImpl(SequencedCollection<PaginationLayout> paginationLayouts) {
        this(paginationLayouts, new ContainerConfig(true, null, false));
    }

    @Override
    public Pagination container(boolean container) {
        return new PaginationImpl(paginationLayouts, new ContainerConfig(container,null, false));
    }

    @Override
    public Pagination color(@Nullable Integer color) {
        return new PaginationImpl(paginationLayouts, new ContainerConfig(true, color, config().spoiler()));
    }

    @Override
    public Pagination spoiler(boolean spoiler) {
        return new PaginationImpl(paginationLayouts, new ContainerConfig(true, config.color(), spoiler));
    }

    record ContainerConfig(boolean active, @Nullable Integer color, boolean spoiler) { }
}
