package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.definitions.interactions.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.DispatchingContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.AutoCompleteEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@ApiStatus.Internal
public final class AutoCompleteHandler extends EventHandler<CommandAutoCompleteInteractionEvent> {

    public AutoCompleteHandler(DispatchingContext dispatchingContext) {
        super(dispatchingContext);
    }

    @Override
    protected InvocationContext<CommandAutoCompleteInteractionEvent> prepare(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull Runtime runtime) {
        CommandAutoCompleteInteraction interaction = event.getInteraction();
        Optional<AutoCompleteDefinition> autoComplete;

        autoComplete = registry.find(AutoCompleteDefinition.class,
                it -> it.commands().stream()
                        .anyMatch(rule -> interaction.getFullCommandName().startsWith(rule.command()))
        ).stream().findFirst();

        if (autoComplete.isEmpty()) {
            var command = registry.find(SlashCommandDefinition.class,
                    it -> it.name().equals(interaction.getFullCommandName())
            ).stream().findFirst();
            if (command.isPresent()) {
                autoComplete = registry.find(AutoCompleteDefinition.class,
                        it -> it.commands().stream()
                                .anyMatch(rule -> command.get().methodDescription().name().equals(rule.command()))
                ).stream().findFirst();
            }
        }

        return autoComplete.map(it ->
                new InvocationContext<>(event, runtime.keyValueStore(), it, List.of(new AutoCompleteEvent(event, registry, runtime)))
        ).orElseGet(() -> {
            log.debug("No auto complete handler found for {}", interaction.getFullCommandName());
            return null;
        });
    }
}
