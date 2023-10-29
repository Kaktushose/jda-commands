package com.github.kaktushose.jda.commands.dispatching.interactions.commands;

import com.github.kaktushose.jda.commands.dispatching.DispatcherSupervisor;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor.InteractionRuntime;
import com.github.kaktushose.jda.commands.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.dispatching.filter.FilterRegistry.FilterPosition;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericDispatcher;
import com.github.kaktushose.jda.commands.dispatching.reply.ReplyContext;
import com.github.kaktushose.jda.commands.embeds.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.interactions.SlashCommandDefinition;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Dispatches commands by taking a {@link CommandContext} and passing it through the execution chain.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 2.0.0
 */
public class CommandDispatcher extends GenericDispatcher<CommandContext> {

    private static final Logger log = LoggerFactory.getLogger(CommandDispatcher.class);
    private final RuntimeSupervisor runtimeSupervisor;

    /**
     * Constructs a new ButtonDispatcher.
     *
     * @param supervisor        the {@link DispatcherSupervisor} which supervises this dispatcher.
     * @param runtimeSupervisor the corresponding {@link RuntimeSupervisor}
     */
    public CommandDispatcher(DispatcherSupervisor supervisor, RuntimeSupervisor runtimeSupervisor) {
        super(supervisor);
        this.runtimeSupervisor = runtimeSupervisor;
    }

    /**
     * Dispatches a {@link CommandContext}. This will route the command, apply all filters and parse the arguments.
     * Finally, the command will be executed.
     *
     * @param context the {@link CommandContext} to dispatch.
     */
    public void onEvent(CommandContext context) {
        ErrorMessageFactory messageFactory = implementationRegistry.getErrorMessageFactory();

        log.debug("Applying filters in phase BEFORE_ROUTING...");
        for (Filter filter : filterRegistry.getAll(FilterPosition.BEFORE_ROUTING)) {
            filter.apply(context);
            if (checkCancelled(context)) {
                return;
            }
        }

        Optional<SlashCommandDefinition> optional = interactionRegistry.getCommands().stream()
                .filter(it -> it.getName().equals(context.getEvent().getFullCommandName()))
                .findFirst();
        if (optional.isEmpty()) {
            IllegalStateException exception = new IllegalStateException(
                    "No slash command found! Please report this error the the devs of jda-commands."
            );
            context.setCancelled(true).setErrorMessage(messageFactory.getCommandExecutionFailedMessage(context, exception));
            checkCancelled(context);
            throw exception;
        }

        SlashCommandDefinition command = optional.get();
        context.setCommand(command).setEphemeral(command.isEphemeral());
        log.debug("Input matches command: {}", command);


        List<String> parameters = new ArrayList<>();
        Map<String, OptionMapping> options = context.getOptionsAsMap();
        command.getActualParameters().forEach(param -> {
            if (!options.containsKey(param.getName())) {
                return;
            }
            parameters.add(options.get(param.getName()).getAsString());
        });
        context.setInput(parameters.toArray(new String[]{}));

        log.debug("Applying filters in phase BEFORE_ADAPTING...");
        for (Filter filter : filterRegistry.getAll(FilterPosition.BEFORE_ADAPTING)) {
            filter.apply(context);
            if (checkCancelled(context)) {
                return;
            }
        }

        adapterRegistry.adapt(context);
        if (checkCancelled(context)) {
            return;
        }

        log.debug("Applying filters in phase BEFORE_EXECUTION...");
        for (Filter filter : filterRegistry.getAll(FilterPosition.BEFORE_EXECUTION)) {
            filter.apply(context);
            if (checkCancelled(context)) {
                return;
            }
        }

        log.info("Executing command {} for user {}", command.getMethod().getName(), context.getEvent().getMember());
        try {
            InteractionRuntime runtime = runtimeSupervisor.newRuntime(context.getEvent(), command);
            context.setRuntime(runtime);
            log.debug("Invoking method with following arguments: {}", context.getArguments());
            command.getMethod().invoke(runtime.getInstance(), context.getArguments().toArray());
        } catch (Exception exception) {
            log.error("Command execution failed!", exception);
            // this unwraps the underlying error in case of an exception inside the command class
            Throwable throwable = exception instanceof InvocationTargetException ? exception.getCause() : exception;
            context.setCancelled(true).setErrorMessage(messageFactory.getCommandExecutionFailedMessage(context, throwable));
            checkCancelled(context);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean checkCancelled(CommandContext context) {
        if (context.isCancelled()) {
            ReplyContext replyContext = new ReplyContext(context);
            replyContext.getBuilder().applyData(context.getErrorMessage());
            replyContext.queue();
            return true;
        }
        return false;
    }
}
