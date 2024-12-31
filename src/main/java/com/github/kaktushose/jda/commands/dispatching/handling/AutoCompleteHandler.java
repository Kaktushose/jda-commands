package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.definitions.interactions.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
public final class AutoCompleteHandler extends EventHandler<CommandAutoCompleteInteractionEvent> {

    public AutoCompleteHandler(DispatchingContext dispatchingContext) {
        super(dispatchingContext);
    }

    @Override
    protected InvocationContext<CommandAutoCompleteInteractionEvent> prepare(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull Runtime runtime) {
        CommandAutoCompleteInteraction interaction = event.getInteraction();

        return registry.find(AutoCompleteDefinition.class,
                        it -> it.commands().stream().anyMatch(name -> interaction.getFullCommandName().startsWith(name))
                ).stream()
                .findFirst()
                .map(autoComplete -> new InvocationContext<>(event, runtime.keyValueStore(), autoComplete, List.of(new AutoCompleteEvent(event, registry, runtime))))
                .orElseGet(() -> {
                    log.debug("No auto complete handler found for {}", interaction.getFullCommandName());
                    return null;
                });
    }
}
