package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.Page;
import io.github.kaktushose.jdac.components.pagination.PaginationLayout;
import io.github.kaktushose.jdac.components.pagination.internal.DynamicImpl;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;

import java.util.SequencedCollection;
import java.util.function.Function;

/// A type of [PaginationLayout] that is as the name says *dynamic*. This means its content is dependent on the
/// pagination state, e.g. the current page number.
///
/// @see Page
public non-sealed interface Dynamic extends PaginationLayout, Threshold {

    /// Creates a new [Dynamic]. The passed [Function] takes a [Page] and must return a [SequencedCollection] of
    /// [ContainerChildComponent]s to show for the current page.
    ///
    /// @param bodyFunction the [Function] to render the current page with
    /// @return a new [Dynamic]
    static Dynamic of(Function<Page, SequencedCollection<ContainerChildComponent>> bodyFunction) {
        return new DynamicImpl(bodyFunction);
    }

    @Override
    Dynamic threshold(int threshold);

    /// Gets the function to render the page
    ///
    /// @return the function to render the page
    Function<Page, SequencedCollection<ContainerChildComponent>> function();
}
