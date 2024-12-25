package com.github.kaktushose.jda.commands.dispatching.middleware;

import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.function.Consumer;

/**
 * Middlewares run just before an interaction event gets dispatched. They are used to perform additional checks or add
 * more info the {@link InvocationContext}. Either register them at the {@link MiddlewareRegistry} or use the
 * {@link com.github.kaktushose.jda.commands.annotations.Implementation Implementation} annotation. Middlewares can have
 * different {@link Priority Priorities} dictating their priority on execution.
 *
 * @see com.github.kaktushose.jda.commands.annotations.Implementation Implementation
 * @see MiddlewareRegistry
 * @since 4.0.0
 */
@FunctionalInterface
public interface Middleware extends Consumer<InvocationContext<?>> {

    /**
     * Executes this middleware with the given {@link InvocationContext}. Use {@link InvocationContext#cancel(MessageCreateData)}
     * to cancel the execution chain.
     *
     * @param context the {@link InvocationContext} of the current interaction event
     */
    void accept(InvocationContext<?> context);

}
