package com.github.kaktushose.jda.commands.dispatching.refactor.handling;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.autocomplete.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.dispatching.refactor.ExecutionContext;
import com.github.kaktushose.jda.commands.dispatching.refactor.Runtime;
import com.github.kaktushose.jda.commands.reflect.interactions.AutoCompleteDefinition;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;

import java.util.Optional;

public class AutoCompleteHandler extends EventHandler<com.github.kaktushose.jda.commands.dispatching.refactor.event.jda.AutoCompleteEvent> {

    public AutoCompleteHandler(HandlerContext handlerContext) {
        super(handlerContext);
    }

    @Override
    public void accept(com.github.kaktushose.jda.commands.dispatching.refactor.event.jda.AutoCompleteEvent event, Runtime runtime) {
        CommandAutoCompleteInteraction interaction = event.event().getInteraction();

        Optional<AutoCompleteDefinition> optionalAutoComplete = interactionRegistry.getAutoCompletes().stream()
                .filter(it -> it.getCommandNames().stream().anyMatch(name -> interaction.getFullCommandName().startsWith(name)))
                .findFirst();

        if (optionalAutoComplete.isEmpty()) {
            log.debug("No auto complete handler found for {}", interaction.getFullCommandName());
            return;
        }

        AutoCompleteDefinition autoComplete = optionalAutoComplete.get();
        var context = new ExecutionContext<>(event.event(), autoComplete, runtime, handlerContext);

        executeMiddlewares(context);
        if (checkCancelled(context)) {
            log.debug("Interaction execution cancelled by middleware");
            return;
        }

        log.debug("Input matches auto complete: {}", autoComplete.getDefinitionId());
        log.info("Executing auto complete {} for user {}", autoComplete.getMethod().getName(), interaction.getMember());
        try {
            autoComplete.getMethod().invoke(runtimeSupervisor.newRuntime(autoComplete).getInstance(), new AutoCompleteEvent(context, interactionRegistry));
        } catch (Exception exception) {
            throw new IllegalStateException("Auto complete execution failed!", exception);
        }
    }
}
