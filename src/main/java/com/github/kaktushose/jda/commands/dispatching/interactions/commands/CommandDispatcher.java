package com.github.kaktushose.jda.commands.dispatching.interactions.commands;

import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericDispatcher;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.embeds.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.GenericCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Dispatches command events.
 *
 * @since 4.0.0
 */
public class CommandDispatcher extends GenericDispatcher {

    private static final Logger log = LoggerFactory.getLogger(CommandDispatcher.class);

    /**
     * Constructs a new CommandDispatcher.
     *
     * @param middlewareRegistry
     * @param implementationRegistry
     * @param interactionRegistry
     * @param adapterRegistry
     * @param runtimeSupervisor
     */
    public CommandDispatcher(MiddlewareRegistry middlewareRegistry, ImplementationRegistry implementationRegistry, InteractionRegistry interactionRegistry, TypeAdapterRegistry adapterRegistry, RuntimeSupervisor runtimeSupervisor) {
        super(middlewareRegistry, implementationRegistry, interactionRegistry, adapterRegistry, runtimeSupervisor);
    }

    @Override
    public void onEvent(Context context) {
        GenericCommandInteractionEvent event = (GenericCommandInteractionEvent) context.getEvent();
        ErrorMessageFactory messageFactory = implementationRegistry.getErrorMessageFactory();

        Optional<GenericCommandDefinition> optional = interactionRegistry.getCommands().stream()
                .filter(it -> it.getName().equals(event.getFullCommandName()))
                .findFirst();

        if (optional.isEmpty()) {
            IllegalStateException exception = new IllegalStateException("No command found! Please report this error the the devs of jda-commands.");
            context.setCancelled(messageFactory.getCommandExecutionFailedMessage(context, exception));
            checkCancelled(context);
            throw exception;
        }

        GenericCommandDefinition command = optional.get();
        context.setInteractionDefinition(command).setEphemeral(command.isEphemeral());
        log.debug("Input matches command: {}", command.getDefinitionId());

        List<Object> arguments;
        if (command.getCommandType() == Command.Type.SLASH) {
            SlashCommandDefinition slashCommand = (SlashCommandDefinition) command;
            SlashCommandContext slashContext = (SlashCommandContext) context;
            slashContext.setCommand(slashCommand);

            Map<String, OptionMapping> options = slashContext.getOptionsAsMap();
            List<String> parameters = new ArrayList<>();
            slashCommand.getActualParameters().forEach(param -> {
                if (!options.containsKey(param.name())) {
                    return;
                }
                parameters.add(options.get(param.name()).getAsString());
            });
            slashContext.setInput(parameters.toArray(new String[]{}));

            adapterRegistry.adapt(slashContext);
            if (checkCancelled(slashContext)) {
                return;
            }

            arguments = slashContext.getArguments();
            arguments.addFirst(new CommandEvent(context, interactionRegistry));
        } else {
            arguments = new ArrayList<>() {{
                add(new CommandEvent(context, interactionRegistry));
                add(((GenericContextInteractionEvent<?>) event).getTarget());
            }};
        }

        executeMiddlewares(context);
        if (checkCancelled(context)) {
            log.debug("Interaction execution cancelled by middleware");
            return;
        }

        log.info("Executing command {} for user {}", command.getMethod().getName(), context.getEvent().getMember());
        try {
            RuntimeSupervisor.InteractionRuntime runtime = runtimeSupervisor.newRuntime(event, command);
            context.setRuntime(runtime);
            log.debug("Invoking method with following arguments: {}", arguments);
            command.getMethod().invoke(runtime.getInstance(), arguments.toArray());
        } catch (Exception exception) {
            log.error("Command execution failed!", exception);
            // this unwraps the underlying error in case of an exception inside the command class
            Throwable throwable = exception instanceof InvocationTargetException ? exception.getCause() : exception;
            context.setCancelled(messageFactory.getCommandExecutionFailedMessage(context, throwable));
            checkCancelled(context);
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
