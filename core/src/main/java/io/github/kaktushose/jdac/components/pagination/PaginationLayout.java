package io.github.kaktushose.jdac.components.pagination;

import io.github.kaktushose.jdac.components.pagination.layout.Content;
import io.github.kaktushose.jdac.components.pagination.layout.ControlRow;
import io.github.kaktushose.jdac.message.placeholder.Entry;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/// PaginationLayouts make up a [Pagination]. You can think of them like child components of a top level component.
///
/// ## Example
/// ```
/// Pagination.of(
///     Content.text("Pagination Example"),
///     Content.of(Separator.createDivider(Spacing.SMALL)),
///     Content.of(page -> List.of(TextDisplay.of("Page: %d".formatted(page.currentPage())))),
///     ControlRow.of(Control.forward("onForward"), Control.backward("onBackward"))
/// );
/// ```
///
/// @see Content
/// @see ControlRow
public sealed interface PaginationLayout permits ControlRow, Content {

    /// Gets the [Predicate] that must be matched before the [PaginationLayout] shows up.
    ///
    /// @return the [Predicate]
    Predicate<Page> predicate();

    /// Sets a [Predicate] that must be matched before the [PaginationLayout] shows up.
    ///
    /// For instance, this can be useful to only show [Content] starting at a certain page number:
    /// ```java
    /// Content.text("This pagination has at least 3 pages").predicate(page -> page.currentPage >= 3)
    /// ```
    ///
    /// @param predicate the [Predicate] that must be matched
    /// @return the [Predicate]
    PaginationLayout predicate(Predicate<Page> predicate);

    /// Gets the [Entries][Entry] to use for localization.
    ///
    /// @return the [Entries][Entry] to use for localization
    List<Entry> entries();

    /// Adds [Entries][Entry] to use for localization.
    ///
    /// @param entries the [Entries][Entry]
    /// @return this instance for fluent interface
    default PaginationLayout entries(Entry... entries) {
        return entries(Arrays.asList(entries));
    }

    /// Adds [Entries][Entry] to use for localization.
    ///
    /// @param entries the [Entries][Entry]
    /// @return this instance for fluent interface
    PaginationLayout entries(Collection<Entry> entries);

}
