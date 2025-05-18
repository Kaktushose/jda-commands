package com.github.kaktushose.jda.commands.dispatching.handling.command;

import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
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
                .map(args -> new InvocationContext<>(
                        event,
                        runtime.keyValueStore(),
                        command,
                        Helpers.replyConfig(command, dispatchingContext.globalReplyConfig()),
                        args)
                ).orElse(null);
    }

    private Optional<List<Object>> parseArguments(SlashCommandDefinition command, SlashCommandInteractionEvent event, Runtime runtime) {
        var input = command.commandOptions().stream()
                .map(it -> event.getOption(it.name()))
                .toList();
        InteractionDefinition.ReplyConfig replyConfig = Helpers.replyConfig(command, dispatchingContext.globalReplyConfig());
        List<Object> parsedArguments = new ArrayList<>();

        log.debug("Type adapting arguments...");
        var commandOptions = List.copyOf(command.commandOptions());
        parsedArguments.addFirst(new CommandEvent(event, registry, runtime, command, replyConfig));

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

            if (optionMapping == null) {
                parsedArguments.add(TypeAdapters.DEFAULT_MAPPINGS.getOrDefault(type, null));
                continue;
            }

            log.debug("Trying to adapt input \"{}\" to type {}", optionMapping.getAsString(), type.getName());
            Optional<?> parsed = Optional.of(optionMapping)
                    .map(mapping -> switch (mapping.getType()) {
                        case USER -> {
                            if (Member.class.isAssignableFrom(type)) {
                                yield mapping.getAsMember();
                            }
                            yield mapping.getAsUser();
                        }
                        case ROLE -> mapping.getAsRole();
                        case CHANNEL -> mapping.getAsChannel();
                        default -> adapter.apply(mapping.getAsString(), event);
                    });

            if (parsed.isEmpty()) {
                log.debug("Type adapting failed!");
                MessageCreateDataReply.reply(event, command, replyConfig,
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
