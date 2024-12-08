package com.github.kaktushose.jda.commands.dispatching.refactor.handling;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.SlashCommandContext;
import com.github.kaktushose.jda.commands.dispatching.refactor.DispatcherContext;
import com.github.kaktushose.jda.commands.dispatching.refactor.Runtime;
import com.github.kaktushose.jda.commands.dispatching.refactor.event.jda.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.ContextCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.GenericCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class CommandHandler extends EventHandler<CommandEvent> {

    public CommandHandler(DispatcherContext dispatcherContext) {
        super(dispatcherContext);
    }

    @Override
    public void accept(CommandEvent commandEvent, Runtime runtime) {
        var messageFactory = implementationRegistry.getErrorMessageFactory();
        Context genericContext;
        GenericCommandDefinition genericCommand;
        List<Object> arguments;
        switch (commandEvent) {
            case CommandEvent.SlashCommandEvent(SlashCommandInteractionEvent slashEvent) -> {
                var context = new SlashCommandContext(slashEvent, interactionRegistry, implementationRegistry);
                var command = interactionRegistry.getCommandDefinition(slashEvent, SlashCommandDefinition.class);
                context.setCommand(command);

                Map<String, OptionMapping> options = context.getOptionsAsMap();
                var parameters = command.getActualParameters().stream()
                        .filter(it -> options.containsKey(it.name()))
                        .map(it -> options.get(it.name()).getAsString())
                        .toList();

                context.setInput(parameters.toArray(new String[]{}));

                adapterRegistry.adapt(context);
                if (checkCancelled(context)) {
                    return;
                }

                genericCommand = command;
                genericContext = context;
                arguments = context.getArguments();
                arguments.addFirst(new com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandEvent(context, interactionRegistry));
            }
            case CommandEvent.ContextCommandEvent<?>(GenericContextInteractionEvent<?> contextEvent) -> {
                genericContext = new Context(contextEvent, interactionRegistry, implementationRegistry);
                genericCommand = interactionRegistry.getCommandDefinition(contextEvent, ContextCommandDefinition.class);
                arguments = List.of(
                        new com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandEvent(genericContext, interactionRegistry),
                        contextEvent.getTarget()
                );
            }
        }

        genericContext.setInteractionDefinition(genericCommand).setEphemeral(genericCommand.isEphemeral());
        log.debug("Input matches command: {}", genericCommand.getDefinitionId());

        executeMiddlewares(genericContext);
        if (checkCancelled(genericContext)) {
            log.debug("Interaction execution cancelled by middleware");
            return;
        }

        log.info("Executing command {} for user {}", genericCommand.getMethod().getName(), genericContext.getEvent().getMember());
        try {
            log.debug("Invoking method with following arguments: {}", arguments);
            genericCommand.getMethod().invoke(runtime.instance(genericCommand), arguments.toArray());
        } catch (Exception exception) {
            log.error("Command execution failed!", exception);
            // this unwraps the underlying error in case of an exception inside the command class
            Throwable throwable = exception instanceof InvocationTargetException ? exception.getCause() : exception;
            genericContext.setCancelled(messageFactory.getCommandExecutionFailedMessage(genericContext, throwable));
            checkCancelled(genericContext);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private boolean checkCancelled(Context context) {
        if (context.isCancelled()) {
            ReplyContext replyContext = new ReplyContext(context);
            replyContext.getBuilder().applyData(context.getErrorMessage());
            replyContext.queue();
            return true;
        }
        return false;
    }
}
