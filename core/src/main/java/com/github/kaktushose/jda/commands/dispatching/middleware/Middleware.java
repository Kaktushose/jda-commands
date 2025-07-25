package com.github.kaktushose.jda.commands.dispatching.middleware;


import com.github.kaktushose.jda.commands.JDACBuilder;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.function.Consumer;

/// Middlewares run just before an interaction event gets dispatched. They are used to perform additional checks or add
/// more info the [InvocationContext]. Middlewares can have different [Priorities][Priority] dictating their priority
/// on execution.
///
/// Register them at the [JDACBuilder#middleware(Priority, Middleware)]  or use the
/// [`@Implementation.Validator`]({@docRoot}/io.github.kaktushose.jda.commands.extension.guice/com/github/kaktushose/jda/commands/guice/Implementation.Middleware.html)
/// annotation of the guice extension.
///
/// ### Example
/// ```java
/// @Middleware(priority = Priority.NORMAL)
/// public class CustomMiddleware implements Middleware {
///     private static final Logger log = LoggerFactory.getLogger(FirstMiddleware.class);
///
///     @Override
///     public void accept(InvocationContext<?> context) {
///         log.info("run custom middleware");
///     }
/// }
/// ```
@FunctionalInterface
public interface Middleware extends Consumer<InvocationContext<?>> {

    /// Executes this middleware with the given [InvocationContext]. Use [InvocationContext#cancel(MessageCreateData)] to cancel the execution chain.
    ///
    /// @param context the [InvocationContext] of the current interaction event
    void accept(InvocationContext<?> context);

}
