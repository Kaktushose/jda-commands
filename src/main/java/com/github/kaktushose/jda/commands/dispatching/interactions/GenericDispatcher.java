package com.github.kaktushose.jda.commands.dispatching.interactions;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for event dispatchers.
 *
 * @since 4.0.0
 */
public abstract class GenericDispatcher {

    private final static Logger log = LoggerFactory.getLogger(GenericDispatcher.class);

    protected final JDACommands jdaCommands;
    protected final MiddlewareRegistry middlewareRegistry;
    protected final ImplementationRegistry implementationRegistry;
    protected final InteractionRegistry interactionRegistry;
    protected final TypeAdapterRegistry adapterRegistry;
    protected final RuntimeSupervisor runtimeSupervisor;

    /**
     * Constructs a new GenericDispatcher.
     *
     * @param jdaCommands the corresponding {@link JDACommands} instance.
     */
    public GenericDispatcher(JDACommands jdaCommands) {
        this.jdaCommands = jdaCommands;
        middlewareRegistry = jdaCommands.getMiddlewareRegistry();
        implementationRegistry = jdaCommands.getImplementationRegistry();
        interactionRegistry = jdaCommands.getInteractionRegistry();
        adapterRegistry = jdaCommands.getAdapterRegistry();
        runtimeSupervisor = jdaCommands.getRuntimeSupervisor();
    }

    protected void executeMiddlewares(Context context) {
        log.debug("Executing middlewares...");

        for (Middleware middleware : middlewareRegistry.getMiddlewares(Priority.PERMISSIONS)) {
            log.debug("Executing middleware {}", middleware.getClass().getSimpleName());
            middleware.execute(context);
            if (context.isCancelled()) {
                return;
            }
        }
        for (Middleware middleware : middlewareRegistry.getMiddlewares(Priority.HIGH)) {
            log.debug("Executing middleware {}", middleware.getClass().getSimpleName());
            middleware.execute(context);
            if (context.isCancelled()) {
                return;
            }
        }
        for (Middleware middleware : middlewareRegistry.getMiddlewares(Priority.NORMAL)) {
            log.debug("Executing middleware {}", middleware.getClass().getSimpleName());
            middleware.execute(context);
            if (context.isCancelled()) {
                return;
            }
        }
        for (Middleware middleware : middlewareRegistry.getMiddlewares(Priority.LOW)) {
            log.debug("Executing middleware {}", middleware.getClass().getSimpleName());
            middleware.execute(context);
            if (context.isCancelled()) {
                return;
            }
        }
    }

    /**
     * Dispatches a {@link Context}.
     *
     * @param context the {@link Context} to dispatch.
     */
    public abstract void onEvent(Context context);

}
