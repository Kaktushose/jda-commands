package io.github.kaktushose.jdac.dispatching.middleware;

/// Enum to define with witch priority a [Middleware] should be executed.
///
/// @see io.github.kaktushose.jdac.dispatching.middleware.internal.Middlewares
// !! order matters, because enums are compared by ordinal number (java.lang.Comparable)
public enum Priority {
    /// Middlewares with priority PERMISSIONS will always be executed first
    PERMISSIONS,
    /// Highest priority for custom implementation, will be executed right after internal middlewares.
    HIGH,
    /// Default priority.
    NORMAL,
    /// Lowest priority, will be executed at the end
    LOW
}
