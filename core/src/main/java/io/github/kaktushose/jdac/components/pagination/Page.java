package io.github.kaktushose.jdac.components.pagination;

import io.github.kaktushose.jdac.components.pagination.layout.Dynamic;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/// Contains information about a page of a [Pagination] that will be rendered by a [Dynamic].
///
/// You can retrieve the current page number, if present, the maximum number of pages or cancel the pagination.
///
/// Cancellation means that the [Pagination] has reached its last page and the [Pagination#maxPages(int)] should be set
/// to the current page number.
///
/// For instance, this could be useful for [Pagination]s that load their data from a webservice or database, where the
/// maximum number of pages ([Pagination#maxPages(int)]) isn't known yet when the [Pagination] is first sent.
public class Page {

    private final int currentPage;
    private Pagination pagination;
    private @Nullable Integer maxPages;

    /// Constructs a new [Page]. This constructor has internal api status and  normally shouldn't be called by the user.
    ///
    /// @param pagination the corresponding [Pagination] instance
    @ApiStatus.Internal
    public Page(Pagination pagination) {
        this.pagination = pagination;
        this.currentPage = pagination.currentPage();
        this.maxPages = pagination.maxPages();
    }

    /// Indicates that the last page of the [Pagination] has been reached, by setting [Pagination#maxPages(int)] to
    /// the [Pagination#currentPage()] number.
    ///
    /// Use [#cancel(int)] to set a custom limit.
    ///
    /// @return the [Pagination] instance of this page
    public Pagination cancel() {
        return cancel(currentPage);
    }

    /// Indicates that the last page of the [Pagination] has been reached, by setting [Pagination#maxPages(int)] to
    /// the given value.
    ///
    /// @param page the maximum number of pages
    /// @return the [Pagination] instance of this page
    public Pagination cancel(int page) {
        pagination = pagination.maxPages(page);
        maxPages = page;
        return pagination;
    }

    /// Gets the current page number.
    ///
    /// @return the current page number
    public int currentPage() {
        return currentPage;
    }

    /// Gets the maximum number of pages.
    ///
    /// @return an [Optional] holding the maximum number of pages, or an empty [Optional]
    public Optional<Integer> maxPages() {
        return Optional.ofNullable(maxPages);
    }
}
