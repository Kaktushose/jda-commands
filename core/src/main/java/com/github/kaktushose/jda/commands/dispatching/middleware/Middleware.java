package com.github.kaktushose.jda.commands.dispatching.middleware;


import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

/// Middlewares run just before an interaction event gets dispatched. They are used to perform additional checks or add
/// more info to the [InvocationContext]. Middlewares can have different [Priorities][Priority] dictating their priority
/// on execution.
///
/// Register them at the [com.github.kaktushose.jda.commands.JDACBuilder#middleware(Priority, Middleware)]  or use the
/// [`@Implementation.Validator`]({@docRoot}/io.github.kaktushose.jda.commands.extension.guice/com/github/kaktushose/jda/commands/guice/Implementation.Middleware.html)
/// annotation of the guice extension.
///
/// If you want a [Middleware] to only run for certain interaction controllers, just implement [#runFor()]
/// returning the classes of the interaction controllers for which the middleware should run.
///
/// ### Example
/// ```java
/// @Middleware(priority = Priority.NORMAL)
/// public class CustomMiddleware implements Middleware {
///     private static final Logger log = LoggerFactory.getLogger(CustomMiddleware.class);
///
///     @Override
///     public void accept(InvocationContext<?> context) {
///         log.info("run custom middleware");
///     }
/// }
/// ```
///
/// ### Example (run only for specific interaction controller)
/// ```java
/// @Middleware(priority = Priority.NORMAL)
/// public class CustomMiddleware implements Middleware {
///     private static final Logger log = LoggerFactory.getLogger(CustomMiddleware.class);
///
///     @Override
///     public void accept(InvocationContext<?> context) {
///         log.info("run custom middleware");
///     }
///
///     @Override
///     public Collection<Class<?>> runFor() {
///         return List.of(HelloController.class);
///     }
/// }
/// ```
@FunctionalInterface
public interface Middleware extends Consumer<InvocationContext<?>> {

    /// Executes this middleware with the given [InvocationContext]. Use [InvocationContext#cancel(MessageCreateData)] to cancel the execution chain.
    ///
    /// @param context the [InvocationContext] of the current interaction event
    void accept(InvocationContext<?> context);

    /// Declares the interaction controllers for which this middleware should run.
    ///
    /// If this method returns `null`, then this [Middleware] runs for all interaction controllers.
    ///
    /// @return the classes of the interaction controllers or null (run for all interaction controllers)
    @Nullable
    default Collection<Class<?>> runFor() {
        return null;
    }
}
