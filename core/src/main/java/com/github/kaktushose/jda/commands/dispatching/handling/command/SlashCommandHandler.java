package com.github.kaktushose.jda.commands.dispatching.handling.command;

import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.DispatchingContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;
import com.github.kaktushose.jda.commands.dispatching.reply.MessageReply;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApiStatus.Internal
public final class SlashCommandHandler extends EventHandler<SlashCommandInteractionEvent> {

    public SlashCommandHandler(DispatchingContext dispatchingContext) {
        super(dispatchingContext);
    }

    @Override
    protected InvocationContext<SlashCommandInteractionEvent> prepare(@NotNull SlashCommandInteractionEvent event, @NotNull Runtime runtime) {
        SlashCommandDefinition command = registry.find(SlashCommandDefinition.class, true, it ->
                it.name().equals(event.getFullCommandName())
        );

        return parseArguments(command, event, runtime)
                .map(args -> new InvocationContext<>(event, runtime.keyValueStore(), command, args))
                .orElse(null);

    }

    private Optional<List<Object>> parseArguments(SlashCommandDefinition command, SlashCommandInteractionEvent event, Runtime runtime) {
        var input = command.commandOptions().stream()
                .map(it -> Optional.ofNullable(event.getOption(it.name())).map(OptionMapping::getAsString))
                .map(it -> it.orElse(null))
                .toList();

        List<Object> parsedArguments = new ArrayList<>();

        log.debug("Type adapting arguments...");
        var commandOptions = List.copyOf(command.commandOptions());
        parsedArguments.addFirst(new CommandEvent(event, registry, runtime, command, dispatchingContext.globalReplyConfig()));

        if (input.size() != commandOptions.size()) {
            throw new IllegalStateException(
                    "Command input doesn't match command options length! Please report this error the the devs of jda-commands."
            );
        }

        for (int i = 0; i < commandOptions.size(); i++) {
            var commandOption = commandOptions.get(i);
            var raw = input.get(i);
            if (raw == null) {
                if (commandOption.defaultValue() == null) {
                    parsedArguments.add(TypeAdapters.DEFAULT_MAPPINGS.getOrDefault(commandOption.type(), null));
                    continue;
                } else {
                    raw = commandOption.defaultValue();
                }
            }
            log.debug("Trying to adapt input \"{}\" to type {}", raw, commandOption.type().getName());

            var adapter = adapterRegistry.get(commandOption.type()).orElseThrow(() ->
                    new IllegalArgumentException(
                            "No type adapter implementation found for %s. Consider implementing one or change the required type"
                                    .formatted(commandOption.type())
                    )
            );

            var parsed = adapter.apply(raw, event);
            if (parsed.isEmpty()) {
                log.debug("Type adapting failed!");
                new MessageReply(event, command, dispatchingContext.globalReplyConfig()).reply(
                        errorMessageFactory.getTypeAdaptingFailedMessage(Helpers.errorContext(event, command), input)
                );
                return Optional.empty();
            }

            parsedArguments.add(parsed.get());
            log.debug("Added \"{}\" to the argument list", parsed.get());
        }
        return Optional.of(parsedArguments);
    }
}
