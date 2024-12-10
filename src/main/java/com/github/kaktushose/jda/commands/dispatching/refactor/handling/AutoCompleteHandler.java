package com.github.kaktushose.jda.commands.dispatching.refactor.handling;

import com.github.kaktushose.jda.commands.dispatching.refactor.ExecutionContext;
import com.github.kaktushose.jda.commands.dispatching.refactor.Runtime;
import com.github.kaktushose.jda.commands.reflect.interactions.AutoCompleteDefinition;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;

import java.util.List;
import java.util.Optional;

public class AutoCompleteHandler extends EventHandler<CommandAutoCompleteInteractionEvent, ExecutionContext<CommandAutoCompleteInteractionEvent, AutoCompleteDefinition>> {

    public AutoCompleteHandler(HandlerContext handlerContext) {
        super(handlerContext);
    }

    @Override
    protected ExecutionContext<CommandAutoCompleteInteractionEvent, AutoCompleteDefinition> prepare(CommandAutoCompleteInteractionEvent event, Runtime runtime) {
        CommandAutoCompleteInteraction interaction = event.getInteraction();

        Optional<AutoCompleteDefinition> optionalAutoComplete = interactionRegistry.getAutoCompletes().stream()
                .filter(it -> it.getCommandNames().stream().anyMatch(name -> interaction.getFullCommandName().startsWith(name)))
                .findFirst();

        if (optionalAutoComplete.isEmpty()) {
            log.debug("No auto complete handler found for {}", interaction.getFullCommandName());
            return null;
        }

        AutoCompleteDefinition autoComplete = optionalAutoComplete.get();
        return new ExecutionContext<>(event, autoComplete, runtime, handlerContext, List.of());
    }

    @Override
    protected void execute(ExecutionContext<CommandAutoCompleteInteractionEvent, AutoCompleteDefinition> context) {
        context.interactionDefinition().invoke(context);
        checkCancelled(context);
    }
}
