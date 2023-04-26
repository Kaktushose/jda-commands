package com.github.kaktushose.jda.commands.dispatching.filter;

import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import org.jetbrains.annotations.NotNull;

/**
 * Generic top level interface for the filter chain. A filter performs filtering tasks on a {@link GenericContext}
 * before execution. A filter might modify the {@link GenericContext} or even cancel the whole event.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public interface Filter {

    /**
     * Performs the filtering on a {@link GenericContext} object. Use {@link GenericContext#setCancelled(boolean)} to
     * indicate that the {@link GenericContext} didn't pass the filter.
     *
     * @param context the {@link GenericContext} to filter
     */
    void apply(@NotNull GenericContext context);

}
