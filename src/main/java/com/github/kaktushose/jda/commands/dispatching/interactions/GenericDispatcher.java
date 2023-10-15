package com.github.kaktushose.jda.commands.dispatching.interactions;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.DispatcherSupervisor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.filter.FilterRegistry;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandDispatcher;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

/**
 * Abstract base class for event dispatchers.
 *
 * @param <T> the subclass of {@link GenericContext} to dispatch
 * @author Kaktushose
 * @version 4.0.0
 * @see CommandDispatcher CommandDispatcher
 * @since 4.0.0
 */
public abstract class GenericDispatcher<T extends GenericContext<? extends GenericInteractionCreateEvent>> {

    protected final FilterRegistry filterRegistry;
    protected final ImplementationRegistry implementationRegistry;
    protected final InteractionRegistry interactionRegistry;
    protected final TypeAdapterRegistry adapterRegistry;

    /**
     * Constructs a new GenericDispatcher.
     *
     * @param supervisor the {@link DispatcherSupervisor} which supervises this dispatcher.
     */
    public GenericDispatcher(DispatcherSupervisor supervisor) {
        JDACommands jdaCommands = supervisor.getJdaCommands();
        filterRegistry = jdaCommands.getFilterRegistry();
        implementationRegistry = jdaCommands.getImplementationRegistry();
        interactionRegistry = jdaCommands.getInteractionRegistry();
        adapterRegistry = jdaCommands.getAdapterRegistry();
    }

    /**
     * Dispatches a {@link GenericContext}.
     *
     * @param context the {@link GenericContext} to dispatch.
     */
    public abstract void onEvent(T context);

}
