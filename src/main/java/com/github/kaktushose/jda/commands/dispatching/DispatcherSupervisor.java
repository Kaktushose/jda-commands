package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandDispatcher;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for {@link GenericDispatcher Dispatchers}. Delegates incoming {@link GenericContext} to the respective
 * {@link GenericDispatcher}.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 4.0.0
 */
public class DispatcherSupervisor {

    private static final Logger log = LoggerFactory.getLogger(DispatcherSupervisor.class);
    private final Map<Class<? extends GenericInteractionCreateEvent>,
            GenericDispatcher<? extends GenericContext<? extends GenericInteractionCreateEvent>>> dispatchers;
    private final JDACommands jdaCommands;

    /**
     * Constructs a new DispatcherSupervisor.
     */
    public DispatcherSupervisor(JDACommands jdaCommands) {
        this.jdaCommands = jdaCommands;
        dispatchers = new HashMap<>();
        register(SlashCommandInteractionEvent.class, new CommandDispatcher(this));
    }

    /**
     * Registers a new {@link GenericDispatcher}.
     *
     * @param event a subtype of {@link GenericInteractionCreateEvent}
     * @param dispatcher the {@link GenericDispatcher} implementation for the event
     */
    public void register(@NotNull Class<? extends GenericInteractionCreateEvent> event,
                         @NotNull GenericDispatcher<? extends GenericContext<? extends GenericInteractionCreateEvent>> dispatcher) {
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

    /**
     * Dispatches a {@link GenericContext} to its respective {@link GenericDispatcher}, e.g.
     * <code>CommandContext -> CommandDispatcher</code>. Prints a warning if no {@link GenericDispatcher} was registered.
     *
     * @param context the {@link GenericContext} to dispatch
     */
    public void onGenericEvent(@NotNull GenericContext<? extends GenericInteractionCreateEvent> context) {
        Class<? extends GenericInteractionCreateEvent> event = context.getEvent().getClass();
        if (!dispatchers.containsKey(event)) {
            log.warn("No dispatcher found for {}", event.getSimpleName());
            return;
        }

        log.debug("Received {}", event.getSimpleName());
        GenericDispatcher dispatcher = dispatchers.get(event);
        log.debug("Calling {}", dispatcher.getClass().getName());

        try {
            dispatcher.onEvent(context);
        } catch (Exception e) {
            //TODO send this as a reply
            log.error("Command execution failed!", e);
        }
    }

    /**
     * Gets the {@link JDACommands} instance.
     *
     * @return the {@link JDACommands} instance
     */
    public JDACommands getJdaCommands() {
        return jdaCommands;
    }
}
