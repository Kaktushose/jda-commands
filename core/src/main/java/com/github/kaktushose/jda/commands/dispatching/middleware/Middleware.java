package com.github.kaktushose.jda.commands.dispatching.middleware;


import com.github.kaktushose.jda.commands.JDACBuilder;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.extension.Implementation;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.function.Consumer;

/// Middlewares run just before an interaction event gets dispatched. They are used to perform additional checks or add
/// more info the [InvocationContext]. Middlewares can have different [Priorities][Priority] dictating their priority
/// on execution.
///
/// Either register them at the [JDACBuilder#middleware(Priority, Middleware)] or use the [Implementation] annotation.
@FunctionalInterface
public interface Middleware extends Consumer<InvocationContext<?>> {

    /// Executes this middleware with the given [InvocationContext]. Use [InvocationContext#cancel(MessageCreateData)] to cancel the execution chain.
    ///
    /// @param context the [InvocationContext] of the current interaction event
    void accept(InvocationContext<?> context);

}
