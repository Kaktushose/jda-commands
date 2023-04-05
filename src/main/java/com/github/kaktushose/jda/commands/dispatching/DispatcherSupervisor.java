package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.buttons.ButtonContext;
import com.github.kaktushose.jda.commands.dispatching.buttons.ButtonDispatcher;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandDispatcher;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
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
    private final Map<Class<? extends GenericContext<? extends GenericInteractionCreateEvent>>,
            GenericDispatcher<? extends GenericContext<? extends GenericInteractionCreateEvent>>> dispatchers;
    private final JDACommands jdaCommands;
    private final RuntimeSupervisor runtimeSupervisor;

    /**
     * Constructs a new DispatcherSupervisor.
     */
    public DispatcherSupervisor(JDACommands jdaCommands) {
        this.jdaCommands = jdaCommands;
        dispatchers = new HashMap<>();
        runtimeSupervisor = new RuntimeSupervisor();
        register(CommandContext.class, new CommandDispatcher(this, runtimeSupervisor));
        register(ButtonContext.class, new ButtonDispatcher(this, runtimeSupervisor));
    }

    /**
     * Registers a new {@link GenericDispatcher}.
     *
     * @param context a subtype of {@link GenericContext}
     * @param dispatcher the {@link GenericDispatcher} implementation for the event
     */
    public void register(@NotNull Class<? extends GenericContext<? extends GenericInteractionCreateEvent>> context,
                         @NotNull GenericDispatcher<? extends GenericContext<? extends GenericInteractionCreateEvent>> dispatcher) {
        dispatchers.put(context, dispatcher);
        log.debug("Registered dispatcher {} for event {}", dispatcher.getClass().getName(), context.getSimpleName());
    }

    /**
     * Unregisters a {@link GenericDispatcher}
     *
     * @param context the {@link GenericContext} to unregister any {@link GenericDispatcher} for
     */
    public void unregister(@NotNull Class<? extends GenericContext<? extends GenericInteractionCreateEvent>> context) {
        dispatchers.remove(context);
        log.debug("Unregistered dispatcher binding for event {}", context.getSimpleName());
    }

    /**
     * Dispatches a {@link GenericContext} to its respective {@link GenericDispatcher}, e.g.
     * <code>CommandContext -> CommandDispatcher</code>. Prints a warning if no {@link GenericDispatcher} was registered.
     *
     * @param context the {@link GenericContext} to dispatch
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void onGenericEvent(@NotNull GenericContext<? extends GenericInteractionCreateEvent> context) {
        Class<?> clazz = context.getClass();
        if (!dispatchers.containsKey(clazz)) {
            log.warn("No dispatcher found for {}", clazz.getSimpleName());
            return;
        }

        log.debug("Received {}", clazz.getSimpleName());
        GenericDispatcher dispatcher = dispatchers.get(clazz);
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
