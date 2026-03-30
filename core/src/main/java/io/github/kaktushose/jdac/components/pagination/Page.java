package io.github.kaktushose.jdac.components.pagination;

import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class Page {

    private final int currentPage;
    private Pagination pagination;
    private @Nullable Integer maxPages;

    public Page(Pagination pagination, int currentPage, @Nullable Integer maxPages) {
        this.pagination = pagination;
        this.currentPage = currentPage;
        this.maxPages = maxPages;
    }

    public Pagination cancel() {
        pagination = pagination.maxPages(currentPage);
        maxPages = currentPage;
        return pagination;
    }

    public int currentPage() {
        return currentPage;
    }

    public Optional<@Nullable Integer> maxPages() {
        return Optional.ofNullable(maxPages);
    }
}
