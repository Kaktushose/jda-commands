package com.github.kaktushose.jda.commands.dispatching.handling.command;

import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.DispatchingContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;
import com.github.kaktushose.jda.commands.dispatching.reply.internal.MessageCreateDataReply;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.entities.Member;
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
                .map(it -> event.getOption(it.name()))
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
            OptionDataDefinition commandOption = commandOptions.get(i);
            Class<?> type = commandOption.type();
            OptionMapping optionMapping = input.get(i);
            var adapter = adapterRegistry.get(type).orElseThrow(() ->
                    new IllegalArgumentException(
                            "No type adapter implementation found for %s. Consider implementing one or change the required type"
                                    .formatted(commandOption.type())
                    )
            );

            Optional<?> parsed;
            if (optionMapping == null) {
                if (commandOption.defaultValue() == null) {
                    parsedArguments.add(TypeAdapters.DEFAULT_MAPPINGS.getOrDefault(type, null));
                    continue;
                } else {
                    log.debug("Trying to adapt input \"{}\" (default value) to type {}", commandOption.defaultValue(), type.getName());
                    parsed = adapter.apply(commandOption.defaultValue(), event);
                }
            } else {
                log.debug("Trying to adapt input \"{}\" to type {}", optionMapping.getAsString(), type.getName());
                parsed = switch (optionMapping.getType()) {
                    case USER -> {
                        if (Member.class.isAssignableFrom(type)) {
                            yield Optional.ofNullable(optionMapping.getAsMember());
                        }
                        yield Optional.of(optionMapping.getAsUser());
                    }
                    case ROLE -> Optional.of(optionMapping.getAsRole());
                    case CHANNEL -> Optional.of(optionMapping.getAsChannel());
                    default -> adapter.apply(optionMapping.getAsString(), event);
                };
            }

            if (parsed.isEmpty()) {
                log.debug("Type adapting failed!");
                MessageCreateDataReply.reply(event, command, dispatchingContext.globalReplyConfig(),
                        errorMessageFactory.getTypeAdaptingFailedMessage(Helpers.errorContext(event, command), input
                                .stream()
                                .map(it -> it == null ? null : it.getAsString())
                                .toList())
                );
                return Optional.empty();
            }

            parsedArguments.add(parsed.get());
            log.debug("Added \"{}\" to the argument list", parsed.get());
        }
        return Optional.of(parsedArguments);
    }
}
