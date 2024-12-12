package com.github.kaktushose.jda.commands.dispatching.handling.command;

import com.github.kaktushose.jda.commands.dispatching.Invocation;
import com.github.kaktushose.jda.commands.dispatching.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;
import com.github.kaktushose.jda.commands.dispatching.handling.HandlerContext;
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
    protected Invocation<SlashCommandInteractionEvent> prepare(SlashCommandInteractionEvent event, Runtime runtime) {
        SlashCommandDefinition command = interactionRegistry.find(SlashCommandDefinition.class,
                it -> it.getName().equals(event.getFullCommandName()));

        InvocationContext<SlashCommandInteractionEvent> context = new InvocationContext<>(event, runtime.keyValueStore(), command, handlerContext, runtime.id().toString());

        var arguments = parseArguments(context);
        if (arguments != null) {
            arguments.addFirst(new CommandEvent(event, interactionRegistry, runtime, context.ephemeral()));
        }
        return new Invocation<>(context, runtime.instanceSupplier(), arguments);
    }

    private List<Object> parseArguments(InvocationContext<SlashCommandInteractionEvent> context) {
        SlashCommandDefinition command = (SlashCommandDefinition) context.definition();
        SlashCommandInteractionEvent event = context.event();
        var input = command.getActualParameters().stream()
                .map(it -> event.getOption(it.name()).getAsString())
                .toArray(String[]::new);

        List<Object> arguments = new ArrayList<>();
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
                context.cancel(messageFactory.getTypeAdaptingFailedMessage(event, command, Arrays.asList(input)));
                return null;
            }

            arguments.add(parsed.get());
            log.debug("Added \"{}\" to the argument list", parsed.get());
        }
        return arguments;
    }
}
