package com.github.kaktushose.jda.commands.dispatching.interactions.commands;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericDispatcher;
import com.github.kaktushose.jda.commands.dispatching.refactor.DispatcherContext;
import com.github.kaktushose.jda.commands.dispatching.refactor.Runtime;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.ContextCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.GenericCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Dispatches command events.
 *
 * @since 4.0.0
 */
public final class CommandDispatcher extends GenericDispatcher {

    private static final Logger log = LoggerFactory.getLogger(CommandDispatcher.class);

    public CommandDispatcher(DispatcherContext dispatcherContext) {
        super(dispatcherContext);
    }

    @Override
    public void onEvent(GenericCommandInteractionEvent genericEvent, Runtime runtime) {
        var messageFactory = implementationRegistry.getErrorMessageFactory();
        Context genericContext;
        GenericCommandDefinition genericCommand;
        List<Object> arguments;
        switch (genericEvent) {
            case SlashCommandInteractionEvent slashEvent -> {
                var context = new SlashCommandContext(slashEvent, interactionRegistry, implementationRegistry);
                var command = interactionRegistry.getCommandDefinition(genericEvent, SlashCommandDefinition.class);
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
                arguments.addFirst(new CommandEvent(context, interactionRegistry));
            }
            case GenericContextInteractionEvent<?> _ -> {
                genericContext = new Context(genericEvent, interactionRegistry, implementationRegistry);
                genericCommand = interactionRegistry.getCommandDefinition(genericEvent, ContextCommandDefinition.class);
                arguments = List.of(
                        new CommandEvent(genericContext, interactionRegistry),
                        ((GenericContextInteractionEvent<?>) genericEvent).getTarget()
                );
            }
            default -> throw new IllegalStateException("Unexpected event type: " + genericEvent);
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
