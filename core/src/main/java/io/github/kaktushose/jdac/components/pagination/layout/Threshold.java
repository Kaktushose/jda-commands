package io.github.kaktushose.jdac.components.pagination.layout;

import io.github.kaktushose.jdac.components.pagination.PaginationLayout;

/// Indicates that a [PaginationLayout] can be limited by a threshold.
///
/// This threshold must be reached before they show up. In other words, the current page number must be equal or greater than
/// then the set threshold for the [PaginationLayout] to show up.
///
/// @see Dynamic
/// @see ControlRow
/// @see Control
public interface Threshold {

    /// Gets the threshold that must be reached for the [PaginationLayout] to show up.
    ///
    /// @return the threshold
    int threshold();

    /// Sets the threshold that must be reached for the [PaginationLayout] to show up.
    ///
    /// @param threshold the threshold
    /// @return this instance for fluent interface
    Threshold threshold(int threshold);
}
