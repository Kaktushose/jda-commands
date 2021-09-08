package com.github.kaktushose.jda.commands.rewrite.dispatching;

import com.github.kaktushose.jda.commands.entities.CommandEvent;
import com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.ParameterAdapter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.ParameterAdapterRegistry;
import com.github.kaktushose.jda.commands.rewrite.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.filter.FilterRegistry;
import com.github.kaktushose.jda.commands.rewrite.dispatching.parser.ParserSupervisor;
import com.github.kaktushose.jda.commands.rewrite.dispatching.parser.impl.MessageParser;
import com.github.kaktushose.jda.commands.rewrite.dispatching.router.CommandRouter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.router.Router;
import com.github.kaktushose.jda.commands.rewrite.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.rewrite.exceptions.CommandException;
import com.github.kaktushose.jda.commands.rewrite.reflect.CommandDefinition;
import com.github.kaktushose.jda.commands.rewrite.reflect.CommandRegistry;
import com.github.kaktushose.jda.commands.rewrite.reflect.ParameterDefinition;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandDispatcher {

    private final Logger log = LoggerFactory.getLogger(CommandDispatcher.class);
    private final Object jda;
    private final boolean isShardManager;
    private final ParserSupervisor parserSupervisor;
    private final FilterRegistry filterRegistry;
    private final ParameterAdapterRegistry adapterRegistry;
    private final ValidatorRegistry validatorRegistry;
    private final CommandRegistry commandRegistry;
    private Router router;

    public CommandDispatcher(Object jda, boolean isShardManager) {
        this.jda = jda;
        this.isShardManager = isShardManager;

        parserSupervisor = new ParserSupervisor(this);
        if (isShardManager) {
            ((ShardManager) jda).addEventListener(parserSupervisor);
        } else {
            ((JDA) jda).addEventListener(parserSupervisor);
        }
        parserSupervisor.register(MessageReceivedEvent.class, new MessageParser());

        router = new CommandRouter();
        filterRegistry = new FilterRegistry();

        adapterRegistry = new ParameterAdapterRegistry();
        validatorRegistry = new ValidatorRegistry();
        commandRegistry = new CommandRegistry(adapterRegistry, validatorRegistry);
        commandRegistry.index();
    }

    public void onEvent(CommandContext context) {
        router.findCommands(context, commandRegistry.getCommands());
        if (context.isCancelled()) {
            log.debug("No matching command found!");
            return;
        }

        CommandDefinition command = context.getCommand();
        log.debug("Input matching command: {}", command);
        List<Object> arguments = new ArrayList<>();
        String[] input = context.getInput();

        log.debug("Type adapting arguments...");
        MessageReceivedEvent event = context.getEvent();
        arguments.add(new CommandEvent(event.getJDA(), event.getResponseNumber(), event.getMessage(), command, null));
        for (int i = 0; i < input.length; i++) {
            // + 1 so we skip the CommandEvent
            ParameterDefinition parameter = command.getParameters().get(i + 1);
            String raw = input[i];

            log.debug("Trying to adapt input \"{}\" to type {}", raw, parameter.getType().getName());

            Optional<ParameterAdapter<?>> adapter = adapterRegistry.get(parameter.getType());
            if (!adapter.isPresent()) {
                log.debug("No type adapter found!");
                return;
            }

            Optional<?> parsed = adapter.get().parse(raw, context);
            if (!parsed.isPresent()) {
                log.debug("Type adapting failed!");
                return;
            }

            arguments.add(parsed.get());
            log.debug("Added {} to the argument list", parsed.get());
        }
        context.setArguments(arguments);

        log.debug("Applying filters...");
        for (Filter filter : filterRegistry.getAll()) {
            filter.apply(context);
            if (context.isCancelled()) {
                return;
            }
        }

        log.info("Executing command {} for user {}", command.getMethod().getName(), event.getAuthor());
        try {
            log.debug("Invoking method with following arguments: {}", arguments);
            command.getMethod().invoke(command.getInstance(), arguments.toArray());
        } catch (Exception e) {
            log.error("Command execution failed!", new InvocationTargetException(e));
        }
    }

    public ParserSupervisor getParserSupervisor() {
        return parserSupervisor;
    }

    public ParameterAdapterRegistry getAdapterRegistry() {
        return adapterRegistry;
    }

    public ValidatorRegistry getValidatorRegistry() {
        return validatorRegistry;
    }

    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public Object getJda() {
        return jda;
    }

    public boolean isShardManager() {
        return isShardManager;
    }

    public ParserSupervisor getEventListener() {
        return parserSupervisor;
    }

    public FilterRegistry getFilterRegistry() {
        return filterRegistry;
    }

    public Router getRouter() {
        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }
}
