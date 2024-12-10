package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.dispatching.ExecutionContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.events.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.reflect.interactions.AutoCompleteDefinition;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;

import java.util.List;
import java.util.Optional;

public class AutoCompleteHandler extends EventHandler<CommandAutoCompleteInteractionEvent> {

    public AutoCompleteHandler(HandlerContext handlerContext) {
        super(handlerContext);
    }

    @Override
    protected ExecutionContext<CommandAutoCompleteInteractionEvent> prepare(CommandAutoCompleteInteractionEvent event, Runtime runtime) {
        CommandAutoCompleteInteraction interaction = event.getInteraction();

        Optional<AutoCompleteDefinition> optionalAutoComplete = interactionRegistry.getAutoCompletes().stream()
                .filter(it -> it.getCommandNames().stream().anyMatch(name -> interaction.getFullCommandName().startsWith(name)))
                .findFirst();

        if (optionalAutoComplete.isEmpty()) {
            log.debug("No auto complete handler found for {}", interaction.getFullCommandName());
            return null;
        }

        AutoCompleteDefinition autoComplete = optionalAutoComplete.get();
        return new ExecutionContext<>(event, autoComplete, runtime, handlerContext, List.of(), context -> new AutoCompleteEvent(context, interactionRegistry));
    }
}
