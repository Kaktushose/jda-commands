package com.github.kaktushose.jda.commands.dispatching.middleware;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * Middlewares run just before an interaction event gets dispatched. They are used to perform additional checks or add
 * more info the {@link Context}. Either register them at the {@link MiddlewareRegistry} or use the
 * {@link com.github.kaktushose.jda.commands.annotations.Implementation Implementation} annotation. Middlewares can have
 * different {@link Priority Priorities} dictating their priority on execution.
 *
 * @see com.github.kaktushose.jda.commands.annotations.Implementation Implementation
 * @see MiddlewareRegistry
 * @since 4.0.0
 */
public interface Middleware {

    /**
     * Executes this middleware with the given {@link Context}. Use {@link Context#setCancelled(MessageCreateData)}
     * to cancel the execution chain.
     *
     * @param context the {@link Context} of the current interaction event
     */
    void execute(Context context);

}
