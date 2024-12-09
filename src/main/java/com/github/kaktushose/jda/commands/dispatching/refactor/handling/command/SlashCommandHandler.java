package com.github.kaktushose.jda.commands.dispatching.refactor.handling.command;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.SlashCommandContext;
import com.github.kaktushose.jda.commands.dispatching.refactor.Runtime;
import com.github.kaktushose.jda.commands.dispatching.refactor.context.CommandExecutionContext;
import com.github.kaktushose.jda.commands.dispatching.refactor.handling.EventHandler;
import com.github.kaktushose.jda.commands.dispatching.refactor.handling.HandlerContext;
import com.github.kaktushose.jda.commands.embeds.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.ParameterDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SlashCommandHandler extends EventHandler<SlashCommandInteractionEvent, CommandExecutionContext<SlashCommandInteractionEvent, SlashCommandDefinition>> {

    public SlashCommandHandler(HandlerContext handlerContext) {
        super(handlerContext);
    }

    @Override
    protected CommandExecutionContext<SlashCommandInteractionEvent, SlashCommandDefinition> prepare(SlashCommandInteractionEvent event, Runtime runtime) {
        SlashCommandDefinition command = interactionRegistry.find(SlashCommandDefinition.class,
                it -> it.getName().equals(event.getFullCommandName()));

        CommandExecutionContext<SlashCommandInteractionEvent, SlashCommandDefinition> context = new CommandExecutionContext<>(event, command, runtime, handlerContext);

        List<Object> arguments = adapt(context);
        arguments.addFirst(new com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandEvent<>(context, interactionRegistry));
        context.arguments().addAll(arguments);

        return context;
    }

    @Override
    protected void execute(CommandExecutionContext<SlashCommandInteractionEvent, SlashCommandDefinition> context, Runtime runtime) {
        SlashCommandDefinition command = context.interactionDefinition();
        List<Object> arguments = context.arguments();

        log.info("Executing command {} for user {}", command.getMethod().getName(), context.event().getMember());
        try {
            log.debug("Invoking method with following arguments: {}", arguments);
            command.getMethod().invoke(runtime.instance(command), arguments.toArray());
        } catch (Exception exception) {
            log.error("Command execution failed!", exception);
            // this unwraps the underlying error in case of an exception inside the command class
            Throwable throwable = exception instanceof InvocationTargetException ? exception.getCause() : exception;
            context.cancel(implementationRegistry.getErrorMessageFactory().getCommandExecutionFailedMessage(context, throwable));
            checkCancelled(context);
        }
    }

    /**
     * Takes a {@link SlashCommandContext} and attempts to type adapt the command input to the type specified by the
     * {@link SlashCommandDefinition}. Cancels the {@link SlashCommandContext} if the type adapting fails.
     *
     * @param context the {@link SlashCommandContext} to type adapt
     */
    private List<Object> adapt(CommandExecutionContext<SlashCommandInteractionEvent, SlashCommandDefinition> context) {
        SlashCommandDefinition command = context.interactionDefinition();

        var input = command.getActualParameters().stream()
                .map(it -> context.event().getOption(it.name()).getAsString())
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
                    IllegalStateException exception = new IllegalStateException(
                            "Command input doesn't match parameter length! Please report this error the the devs of jda-commands."
                    );
                    context.cancel(messageFactory.getCommandExecutionFailedMessage(context, exception));
                    throw exception;
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

            Optional<TypeAdapter<?>> adapter = adapterRegistry.get(parameter.type());
            if (adapter.isEmpty()) {
                throw new IllegalArgumentException("No type adapter found!");
            }

            Optional<?> parsed = adapter.get().apply(raw, context);
            if (parsed.isEmpty()) {
                log.debug("Type adapting failed!");
                context.cancel(messageFactory.getTypeAdaptingFailedMessage(context));
                break;
            }

            arguments.add(parsed.get());
            log.debug("Added \"{}\" to the argument list", parsed.get());
        }
        return arguments;
    }
}
