package com.github.kaktushose.jda.commands.dispatching.handling.command;

import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.handling.DispatchingContext;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;
import com.github.kaktushose.jda.commands.dispatching.reply.MessageReply;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
                .filter(Objects::nonNull)
                .map(OptionMapping::getAsString)
                .toArray(String[]::new);

        List<Object> arguments = new ArrayList<>();
        arguments.addFirst(new CommandEvent(event, registry, runtime, command));

        ErrorMessageFactory messageFactory = implementationRegistry.getErrorMessageFactory();

        log.debug("Type adapting arguments...");
        var parameters = List.copyOf(command.commandOptions());
        for (int i = 0; i < parameters.size(); i++) {
            var parameter = parameters.get(i);

            // if parameter is array don't parse
            if (String[].class.isAssignableFrom(parameter.type())) {
                log.debug("First parameter is String array. Not adapting arguments");
                arguments.add(input);
                break;
            }

            String raw;
            // current parameter index == total amount of input, check if it's optional else cancel context
            if (i >= input.length) {
                if (!parameter.optional()) {
                    throw new IllegalStateException(
                            "Command input doesn't match parameter length! Please report this error the the devs of jda-commands."
                    );
                }

                // if the default value is an empty String (thus not present) add a null value to the argument list
                // else try to type adapt the default value
                if (parameter.defaultValue() == null) {
                    arguments.add(TypeAdapterRegistry.DEFAULT_MAPPINGS.getOrDefault(parameter.type(), null));
                    continue;
                } else {
                    raw = parameter.defaultValue();
                }
            } else {
                raw = input[i];
            }

            log.debug("Trying to adapt input \"{}\" to type {}", raw, parameter.type().getName());

            TypeAdapter<?> adapter = adapterRegistry.get(parameter.type()).orElseThrow(() ->
                    new IllegalArgumentException(
                            "No type adapter implementation found for %s. Consider implementing one or change the required type"
                                    .formatted(parameter.type())
                    )
            );

            Optional<?> parsed = adapter.apply(raw, event);
            if (parsed.isEmpty()) {
                log.debug("Type adapting failed!");
                new MessageReply(event, command).reply(
                        messageFactory.getTypeAdaptingFailedMessage(Helpers.errorContext(event, command), Arrays.asList(input))
                );
                return Optional.empty();
            }

            arguments.add(parsed.get());
            log.debug("Added \"{}\" to the argument list", parsed.get());
        }
        return Optional.of(arguments);
    }
}
