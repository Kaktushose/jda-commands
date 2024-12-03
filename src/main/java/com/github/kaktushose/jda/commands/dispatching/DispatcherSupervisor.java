package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericDispatcher;
import com.github.kaktushose.jda.commands.dispatching.interactions.autocomplete.AutoCompleteDispatcher;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandDispatcher;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.SlashCommandContext;
import com.github.kaktushose.jda.commands.dispatching.interactions.components.ComponentDispatcher;
import com.github.kaktushose.jda.commands.dispatching.interactions.modals.ModalDispatcher;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry for {@link GenericDispatcher Dispatchers}. Delegates incoming {@link Context} to the respective
 * {@link GenericDispatcher}.
 *
 * @since 4.0.0
 */
public class DispatcherSupervisor extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(DispatcherSupervisor.class);
    private final Map<Class<? extends GenericInteractionCreateEvent>, GenericDispatcher> dispatchers;
    private final InteractionRegistry interactionRegistry;
    private final ImplementationRegistry implementationRegistry;

    /**
     * Constructs a new DispatcherSupervisor.
     */
    public DispatcherSupervisor(MiddlewareRegistry middlewareRegistry, ImplementationRegistry implementationRegistry, InteractionRegistry interactionRegistry, TypeAdapterRegistry adapterRegistry, RuntimeSupervisor runtimeSupervisor) {
        dispatchers = new HashMap<>();
        register(GenericCommandInteractionEvent.class, new CommandDispatcher(middlewareRegistry, implementationRegistry, interactionRegistry, adapterRegistry, runtimeSupervisor));
        register(CommandAutoCompleteInteractionEvent.class, new AutoCompleteDispatcher(middlewareRegistry, implementationRegistry, interactionRegistry, adapterRegistry, runtimeSupervisor));
        register(GenericComponentInteractionCreateEvent.class, new ComponentDispatcher(middlewareRegistry, implementationRegistry, interactionRegistry, adapterRegistry, runtimeSupervisor));
        register(ModalInteractionEvent.class, new ModalDispatcher(middlewareRegistry, implementationRegistry, interactionRegistry, adapterRegistry, runtimeSupervisor));
        this.interactionRegistry = interactionRegistry;
        this.implementationRegistry = implementationRegistry;
    }

    /**
     * Registers a new {@link GenericDispatcher}.
     *
     * @param event      a subtype of {@link GenericInteractionCreateEvent}
     * @param dispatcher the {@link GenericDispatcher} implementation for the event
     */
    public void register(@NotNull Class<? extends GenericInteractionCreateEvent> event, @NotNull GenericDispatcher dispatcher) {
        dispatchers.put(event, dispatcher);
        log.debug("Registered dispatcher {} for event {}", dispatcher.getClass().getName(), event.getSimpleName());
    }

    /**
     * Unregisters a {@link GenericDispatcher}
     *
     * @param event the {@link GenericInteractionCreateEvent} to unregister any {@link GenericDispatcher} for
     */
    public void unregister(@NotNull Class<? extends GenericInteractionCreateEvent> event) {
        dispatchers.remove(event);
        log.debug("Unregistered dispatcher binding for event {}", event.getSimpleName());
    }

    @Override
    public void onGenericInteractionCreate(@NotNull GenericInteractionCreateEvent event) {
        Class<?> clazz = event.getClass();
        Optional<Class<? extends GenericInteractionCreateEvent>> key = dispatchers.keySet().stream()
                .filter(it -> it.isAssignableFrom(clazz))
                .findFirst();
        if (key.isEmpty()) {
            log.debug("No dispatcher found for {}", clazz.getSimpleName());
            return;
        }

        Context context;
        if (SlashCommandInteractionEvent.class.isAssignableFrom(clazz)) {
            context = new SlashCommandContext((SlashCommandInteractionEvent) event, interactionRegistry, implementationRegistry);
        } else {
            context = new Context(event, interactionRegistry, implementationRegistry);
        }

        GenericDispatcher dispatcher = dispatchers.get(key.get());
        log.debug("Calling {}", dispatcher.getClass().getName());
        try {
            dispatcher.onEvent(context);
        } catch (Exception e) {
            //TODO send this as a reply
            log.error("Command execution failed!", e);
        }
    }
}
