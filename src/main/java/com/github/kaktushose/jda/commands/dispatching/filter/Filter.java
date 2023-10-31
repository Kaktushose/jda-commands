package com.github.kaktushose.jda.commands.dispatching.filter;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import org.jetbrains.annotations.NotNull;

/**
 * Generic top level interface for the filter chain. A filter performs filtering tasks on a {@link Context}
 * before execution. A filter might modify the {@link Context} or even cancel the whole event.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public interface Filter {

    /**
     * Performs the filtering on a {@link Context} object. Use {@link Context#setCancelled(boolean)} to
     * indicate that the {@link Context} didn't pass the filter.
     *
     * @param context the {@link Context} to filter
     */
    void apply(@NotNull Context context);

}
