package com.github.kaktushose.jda.commands.dispatching.handling.command;

import com.github.kaktushose.jda.commands.dispatching.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;
import com.github.kaktushose.jda.commands.dispatching.handling.HandlerContext;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyBuilder;
import com.github.kaktushose.jda.commands.embeds.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.ParameterDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class SlashCommandHandler extends EventHandler<SlashCommandInteractionEvent> {

    public SlashCommandHandler(HandlerContext handlerContext) {
        super(handlerContext);
    }

    @Override
    protected InvocationContext<SlashCommandInteractionEvent> prepare(SlashCommandInteractionEvent event, Runtime runtime) {
        SlashCommandDefinition command = interactionRegistry.find(SlashCommandDefinition.class,
                it -> it.getName().equals(event.getFullCommandName()));

        return parseArguments(command, event, runtime)
                .map(args -> newContext(event, runtime, command, args))
                .orElse(null);

    }

    private Optional<List<Object>> parseArguments(SlashCommandDefinition command, SlashCommandInteractionEvent event, Runtime runtime) {
        var input = command.getActualParameters().stream()
                .map(it -> event.getOption(it.name()).getAsString())
                .toArray(String[]::new);

        List<Object> arguments = new ArrayList<>();
        arguments.addFirst(new CommandEvent(event, interactionRegistry, runtime, InvocationContext.ephemeral(command)));

        ErrorMessageFactory messageFactory = implementationRegistry.getErrorMessageFactory();

        log.debug("Type adapting arguments...");
        for (int i = 0; i < command.getActualParameters().size(); i++) {
            ParameterDefinition parameter = command.getActualParameters().get(i);

            // if parameter is array don't parse
            if (String[].class.isAssignableFrom(parameter.type())) {
                log.debug("First parameter is String array. Not adapting arguments");
                arguments.add(input);
                break;
            }

            String raw;
            // current parameter index == total amount of input, check if it's optional else cancel context
            if (i >= input.length) {
                if (!parameter.isOptional()) {
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
                ReplyBuilder.reply(event, InvocationContext.ephemeral(command), messageFactory.getTypeAdaptingFailedMessage(event, command, Arrays.asList(input)));
                return Optional.empty();
            }

            arguments.add(parsed.get());
            log.debug("Added \"{}\" to the argument list", parsed.get());
        }
        return Optional.of(arguments);
    }
}
