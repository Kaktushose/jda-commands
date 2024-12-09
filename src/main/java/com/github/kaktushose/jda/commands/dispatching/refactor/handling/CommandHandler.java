package com.github.kaktushose.jda.commands.dispatching.refactor.handling;

import com.github.kaktushose.jda.commands.dispatching.refactor.ExecutionContext;
import com.github.kaktushose.jda.commands.dispatching.refactor.Runtime;
import com.github.kaktushose.jda.commands.dispatching.refactor.event.jda.CommandEvent;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.ContextCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.GenericCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CommandHandler extends EventHandler<CommandEvent> {

    public CommandHandler(HandlerContext handlerContext) {
        super(handlerContext);
    }

    @Override
    public void accept(CommandEvent commandEvent, Runtime runtime) {
        GenericCommandDefinition command;
        ExecutionContext<? extends GenericCommandInteractionEvent, ? extends GenericCommandDefinition> context;
        List<Object> arguments;
        switch (commandEvent) {
            case CommandEvent.SlashCommandEvent(SlashCommandInteractionEvent slashEvent) -> {
                command = interactionRegistry.getCommandDefinition(slashEvent, SlashCommandDefinition.class);
                context = new ExecutionContext<>(slashEvent, command, runtime, handlerContext);
                arguments = adapterRegistry.adapt((ExecutionContext<SlashCommandInteractionEvent, SlashCommandDefinition>) context);
            }
            case CommandEvent.ContextCommandEvent<?>(GenericContextInteractionEvent<?> contextEvent) -> {
                command = interactionRegistry.getCommandDefinition(contextEvent, ContextCommandDefinition.class);
                context = new ExecutionContext<>(contextEvent, command, runtime, handlerContext);
                arguments = new ArrayList<>(List.of(contextEvent.getTarget()));
            }
        }
        arguments.addFirst(new com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandEvent(context, interactionRegistry));
        context.ephemeral(command.isEphemeral());
        log.debug("Input matches command: {}", command.getDefinitionId());
        if (checkCancelled(context)) {
            return;
        }

        executeMiddlewares(context);
        if (checkCancelled(context)) {
            log.debug("Interaction execution cancelled by middleware");
            return;
        }

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
}
