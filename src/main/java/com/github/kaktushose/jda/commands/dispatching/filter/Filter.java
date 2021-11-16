package com.github.kaktushose.jda.commands.dispatching.filter;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;

/**
 * Generic top level interface for the filter chain. A filter performs filtering tasks on a {@link CommandContext}
 * before execution. A filter might modify the {@link CommandContext} or even cancel the whole event.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public interface Filter {

    /**
     * Performs the filtering on a {@link CommandContext} object. Use {@link CommandContext#setCancelled(boolean)} to
     * indicate that the a {@link CommandContext} didn't pass the filter.
     *
     * @param context the {@link CommandContext} to filter
     */
    void apply(CommandContext context);

}
