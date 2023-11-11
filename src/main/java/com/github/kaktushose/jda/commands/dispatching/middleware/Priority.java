package com.github.kaktushose.jda.commands.dispatching.middleware;

/**
 * Enum to define with witch priority a {@link Middleware} should be executed.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 4.0.0
 */
public enum Priority {
    /**
     * Lowest priority, will be executed at the end
     */
    LOW,
    /**
     * Default priority.
     */
    NORMAL,
    /**
     * Highest priority for custom implementation, will be executed right after internal middlewares.
     */
    HIGH,
    /**
     * Middlewares with priority PERMISSIONS will always be executed first
     */
    PERMISSIONS
}
