package com.github.kaktushose.jda.commands.dispatching.handling.command;

import com.github.kaktushose.jda.commands.dispatching.ExecutionContext;
import com.github.kaktushose.jda.commands.dispatching.Runtime;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.events.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.handling.EventHandler;
import com.github.kaktushose.jda.commands.dispatching.handling.HandlerContext;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.embeds.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.ParameterDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SlashCommandHandler extends EventHandler<SlashCommandInteractionEvent> {

    public SlashCommandHandler(HandlerContext handlerContext) {
        super(handlerContext);
    }

    @Override
    protected ExecutionContext<SlashCommandInteractionEvent> prepare(SlashCommandInteractionEvent event, Runtime runtime) {
        SlashCommandDefinition command = interactionRegistry.find(SlashCommandDefinition.class,
                it -> it.getName().equals(event.getFullCommandName()));

        return switch (adapt(event, command)) {
            case Result.Error(MessageCreateData error) -> {
                ReplyContext.reply(event, command.isEphemeral(), error);
                yield null;
            }
            case Result.Ok(List<Object> arguments) ->
                    new ExecutionContext<>(event, command, runtime, handlerContext, arguments,
                            ctx -> new CommandEvent<>(ctx, interactionRegistry));
        };
    }

    private sealed interface Result {
        record Ok(List<Object> objects) implements Result {}

        record Error(MessageCreateData error) implements Result {}
    }

    private Result adapt(SlashCommandInteractionEvent event, SlashCommandDefinition command) {
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
                return new Result.Error(messageFactory.getTypeAdaptingFailedMessage(event, command, Arrays.asList(input)));
            }

            arguments.add(parsed.get());
            log.debug("Added \"{}\" to the argument list", parsed.get());
        }
        return new Result.Ok(arguments);
    }
}
