package com.github.kaktushose.jda.commands.dispatching.refactor.handling.command;

import com.github.kaktushose.jda.commands.dispatching.refactor.Runtime;
import com.github.kaktushose.jda.commands.dispatching.refactor.context.CommandExecutionContext;
import com.github.kaktushose.jda.commands.dispatching.refactor.handling.EventHandler;
import com.github.kaktushose.jda.commands.dispatching.refactor.handling.HandlerContext;
import com.github.kaktushose.jda.commands.reflect.interactions.GenericInteractionDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.ContextCommandDefinition;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ContextCommandHandler extends EventHandler<GenericContextInteractionEvent<?>, CommandExecutionContext<GenericContextInteractionEvent<?>, ContextCommandDefinition>> {

    public ContextCommandHandler(HandlerContext handlerContext) {
        super(handlerContext);
    }

    @Override
    protected CommandExecutionContext<GenericContextInteractionEvent<?>, ContextCommandDefinition> prepare(GenericContextInteractionEvent<?> event, Runtime runtime) {
        ContextCommandDefinition command = interactionRegistry.find(ContextCommandDefinition.class,
                it -> it.getName().equals(event.getFullCommandName()));
        CommandExecutionContext<GenericContextInteractionEvent<?>, ContextCommandDefinition> context = new CommandExecutionContext<>(event, command, runtime, handlerContext);

        context.arguments().addAll(List.of(
                event.getTarget(),
                new com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandEvent<>(context, interactionRegistry)
        ));

        return context;
    }

    @Override
    protected void execute(CommandExecutionContext<GenericContextInteractionEvent<?>, ContextCommandDefinition> context, Runtime runtime) {
        GenericInteractionDefinition command = context.interactionDefinition();
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
}
