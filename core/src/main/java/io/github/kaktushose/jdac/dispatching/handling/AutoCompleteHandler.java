package com.github.kaktushose.jda.commands.dispatching.handling;

import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.FrameworkContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.List;

@ApiStatus.Internal
public final class AutoCompleteHandler extends EventHandler<CommandAutoCompleteInteractionEvent> {

    public AutoCompleteHandler(FrameworkContext context) {
        super(context);
    }

    @Nullable
    @Override
    protected InvocationContext<CommandAutoCompleteInteractionEvent> prepare(CommandAutoCompleteInteractionEvent event, Runtime runtime) {
        CommandAutoCompleteInteraction interaction = event.getInteraction();

        return interactionRegistry.find(SlashCommandDefinition.class, it -> it.name().equals(interaction.getFullCommandName()))
                .stream()
                .findFirst()
                .map(slashCommandDefinition -> slashCommandDefinition.commandOptions().stream()
                        .filter(option -> option.name().equals(event.getFocusedOption().getName()))
                        .findFirst()
                        .map(OptionDataDefinition::autoComplete)
                        .map(definition ->
                                new InvocationContext<>(
                                        new InvocationContext.Utility(context.i18n(), context.messageResolver()),
                                        new InvocationContext.Data<>(
                                            event,
                                            runtime.keyValueStore(),
                                            definition,
                                            Helpers.replyConfig(slashCommandDefinition, context.globalReplyConfig()),
                                            List.of(new AutoCompleteEvent())
                                        )
                                )
                        ).orElseGet(() -> {
                            log.debug("No auto complete handler found for command \"/{}\"", interaction.getFullCommandName());
                            return null;
                        })
                ).orElseGet(() -> {
                    log.debug("Received unknown command \"/{}\"", interaction.getFullCommandName());
                    return null;
                });
    }
}
