package com.github.kaktushose.jda.commands.dispatching.interactions.autocomplete;

import com.github.kaktushose.jda.commands.dispatching.DispatcherSupervisor;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericDispatcher;
import com.github.kaktushose.jda.commands.reflect.interactions.AutoCompleteDefinition;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Dispatches auto complete by taking a {@link AutoCompleteContext} and passing it through the execution chain.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 4.0.0
 */
public class AutoCompleteDispatcher extends GenericDispatcher<AutoCompleteContext> {

    private static final Logger log = LoggerFactory.getLogger(AutoCompleteDispatcher.class);
    private final RuntimeSupervisor runtimeSupervisor;

    /**
     * Constructs a new AutoCompleteDispatcher.
     *
     * @param supervisor        the {@link DispatcherSupervisor} which supervises this dispatcher.
     * @param runtimeSupervisor the corresponding {@link RuntimeSupervisor}
     */
    public AutoCompleteDispatcher(DispatcherSupervisor supervisor, RuntimeSupervisor runtimeSupervisor) {
        super(supervisor);
        this.runtimeSupervisor = runtimeSupervisor;
    }

    @Override
    public void onEvent(AutoCompleteContext context) {
        CommandAutoCompleteInteractionEvent event = context.getEvent();
        Optional<AutoCompleteDefinition> optionalAutoComplete = interactionRegistry.getAutoCompletes().stream()
                .filter(it -> it.getCommandNames().contains(event.getFullCommandName()))
                .findFirst();

        if (optionalAutoComplete.isEmpty()) {
            log.debug("No auto complete handler found for {}", event.getFullCommandName());
            return;
        }

        AutoCompleteDefinition autoComplete = optionalAutoComplete.get();
        log.debug("Input matches auto complete: {}", autoComplete);

        log.info("Executing auto complete {} for user {}", autoComplete.getMethod().getName(), event.getMember());
        try {
            autoComplete.getMethod().invoke(runtimeSupervisor.getOrCreateInstance(event, autoComplete), new AutoCompleteEvent(context));
        } catch (Exception exception) {
            throw new IllegalStateException("Auto complete execution failed!", exception);
        }
    }
}
