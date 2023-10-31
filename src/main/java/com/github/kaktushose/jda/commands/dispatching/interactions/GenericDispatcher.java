package com.github.kaktushose.jda.commands.dispatching.interactions;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.filter.FilterRegistry;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;

/**
 * Abstract base class for event dispatchers.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 4.0.0
 */
public abstract class GenericDispatcher {

    protected final JDACommands jdaCommands;
    protected final FilterRegistry filterRegistry;
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
        filterRegistry = jdaCommands.getFilterRegistry();
        implementationRegistry = jdaCommands.getImplementationRegistry();
        interactionRegistry = jdaCommands.getInteractionRegistry();
        adapterRegistry = jdaCommands.getAdapterRegistry();
        runtimeSupervisor = jdaCommands.getRuntimeSupervisor();
    }

    /**
     * Dispatches a {@link Context}.
     *
     * @param context the {@link Context} to dispatch.
     */
    public abstract void onEvent(Context context);

}
