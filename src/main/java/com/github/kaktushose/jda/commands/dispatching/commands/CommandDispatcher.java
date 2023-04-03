package com.github.kaktushose.jda.commands.dispatching.commands;

import com.github.kaktushose.jda.commands.dispatching.DispatcherSupervisor;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.GenericDispatcher;
import com.github.kaktushose.jda.commands.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.dispatching.filter.FilterRegistry.FilterPosition;
import com.github.kaktushose.jda.commands.dispatching.reply.MessageSender;
import com.github.kaktushose.jda.commands.embeds.help.HelpMessageFactory;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Dispatches commands by taking a {@link GenericContext} and passing it through the execution chain.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public class CommandDispatcher extends GenericDispatcher<CommandContext> {

    private static final Logger log = LoggerFactory.getLogger(CommandDispatcher.class);

    public CommandDispatcher(DispatcherSupervisor dispatcher) {
        super(dispatcher);
    }

    /**
     * Dispatches a {@link GenericContext}. This will route the command, apply all filters and parse the arguments.
     * Finally, the command will be executed.
     *
     * @param context the {@link GenericContext} to dispatch.
     */
    @SuppressWarnings("ConstantConditions")
    public void onEvent(CommandContext context) {
        log.debug("Applying filters in phase BEFORE_ROUTING...");
        for (Filter filter : filterRegistry.getAll(FilterPosition.BEFORE_ROUTING)) {
            filter.apply(context);
            if (checkCancelled(context)) {
                return;
            }
        }

        HelpMessageFactory helpMessageFactory = implementationRegistry.getHelpMessageFactory();
        MessageSender sender = implementationRegistry.getMessageSender();


        Optional<CommandDefinition> optional = interactionRegistry.getCommands().stream()
                .filter(it -> it.getLabel().equals(context.getEvent().getFullCommandName()))
                .findFirst();

        if (optional.isEmpty()) {
            throw new IllegalStateException("no slash command found?");
            // TODO this is a race condition and should produce a error message
        }

        context.setCommand(optional.get());

        context.getEvent().deferReply(context.getCommand().isEphemeral()).queue();

        if (context.isCancelled() && context.isHelpEvent()) {
            log.debug("Sending generic help");
            // TODO sender.sendGenericHelpMessage(context, helpMessageFactory.getGenericHelp(interactionRegistry.getControllers(), context));
            return;
        }

        if (checkCancelled(context)) {
            log.debug("No matching command found!");
            return;
        }

        CommandDefinition command = context.getCommand();
        log.debug("Input matches command: {}", command);

        if (context.isHelpEvent()) {
            log.debug("Sending specific help");
            // TODO sender.sendSpecificHelpMessage(context, helpMessageFactory.getSpecificHelp(context));
            return;
        }


        List<String> parameters = new ArrayList<>();
        Map<String, OptionMapping> options = context.getOptionsAsMap();
        command.getActualParameters().forEach(param -> {
            if (!options.containsKey(param.getName())) {
                return;
            }
            parameters.add(options.get(param.getName()).getAsString());
        });
        if (!parameters.isEmpty()) {
            context.setInput(parameters.toArray(new String[]{}));
        }

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

        if (checkCancelled(context)) {
            return;
        }

        log.info("Executing command {} for user {}", command.getMethod().getName(), context.getEvent().getMember());
        try {
            log.debug("Invoking method with following arguments: {}", context.getArguments());
            command.getMethod().invoke(command.getInstance(), context.getArguments().toArray());
        } catch (Exception e) {
            // TODO bot error reply goes here
            log.error("Command execution failed!", new InvocationTargetException(e));
        }
    }

    private boolean checkCancelled(GenericContext<?> context) {
        if (context.isCancelled()) {
            implementationRegistry.getMessageSender().sendErrorMessage(context, Objects.requireNonNull(context.getErrorMessage()));
            return true;
        }
        return false;
    }


}
