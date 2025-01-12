package com.github.kaktushose.jda.commands.dispatching.middleware;

import com.github.kaktushose.jda.commands.annotations.Implementation;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.middleware.internal.Middlewares;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.function.Consumer;

/// Middlewares run just before an interaction event gets dispatched. They are used to perform additional checks or add
/// more info the [InvocationContext]. Either register them at the [Middlewares] or use the [Implementation]
/// annotation. Middlewares can have different [Priorities][Priority] dictating their priority on execution.
///
/// @see Implementation Implementation
/// @see Middlewares
@FunctionalInterface
public interface Middleware extends Consumer<InvocationContext<?>> {

    /// Executes this middleware with the given [InvocationContext]. Use [InvocationContext#cancel(MessageCreateData)] to cancel the execution chain.
    ///
    /// @param context the [InvocationContext] of the current interaction event
    void accept(InvocationContext<?> context);

}
