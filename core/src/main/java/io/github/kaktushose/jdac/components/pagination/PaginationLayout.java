package io.github.kaktushose.jdac.components.pagination;

import io.github.kaktushose.jdac.components.pagination.layout.ControlRow;
import io.github.kaktushose.jdac.components.pagination.layout.Dynamic;
import io.github.kaktushose.jdac.components.pagination.layout.Static;

/// PaginationLayouts make up a [Pagination]. You can think of them like child components of a top level component.
///
/// ## Example
/// ```
/// Pagination.of(
///     Static.text("Pagination Example"),
///     Static.divider(Spacing.SMALL),
///     Dynamic.of(page -> List.of(TextDisplay.of("Page: %d".formatted(page.currentPage())))),
///     ControlRow.of(Control.forward("onForward"), Control.backward("onBackward"))
/// );
/// ```
///
/// @see Static
/// @see Dynamic
/// @see ControlRow
public sealed interface PaginationLayout permits ControlRow, Dynamic, Static { }
