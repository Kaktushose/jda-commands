package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.filter.FilterRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

public abstract class GenericDispatcher<T extends GenericContext<? extends GenericInteractionCreateEvent>> {

    protected final FilterRegistry filterRegistry;
    protected final ImplementationRegistry implementationRegistry;
    protected final InteractionRegistry interactionRegistry;
    protected final TypeAdapterRegistry adapterRegistry;

    public GenericDispatcher(DispatcherSupervisor supervisor) {
        JDACommands jdaCommands = supervisor.getJdaCommands();
        filterRegistry = jdaCommands.getFilterRegistry();
        implementationRegistry = jdaCommands.getImplementationRegistry();
        interactionRegistry = jdaCommands.getInteractionRegistry();
        adapterRegistry = jdaCommands.getAdapterRegistry();
    }

    public abstract void onEvent(T context);

}
