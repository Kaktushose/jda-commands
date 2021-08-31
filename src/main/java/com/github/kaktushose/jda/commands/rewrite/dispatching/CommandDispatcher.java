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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandDispatcher {

    private final Object jda;
    private final boolean isShardManager;
    private final ParserSupervisor parserSupervisor;
    private final Router router;
    private final FilterRegistry filterRegistry;
    private final ParameterAdapterRegistry adapterRegistry;
    private final ValidatorRegistry validatorRegistry;
    private final CommandRegistry commandRegistry;

    public CommandDispatcher(Object jda, boolean isShardManager) {
        this.jda = jda;
        this.isShardManager = isShardManager;

        parserSupervisor = new ParserSupervisor(this);
        if (isShardManager) {
            ((ShardManager) jda).addEventListener(parserSupervisor);
        } else {
            ((JDA) jda).addEventListener(parserSupervisor);
        }
        parserSupervisor.addBinding(MessageReceivedEvent.class, new MessageParser());

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
            return;
        }

        CommandDefinition command = context.getCommand();
        List<Object> arguments = new ArrayList<>();
        String[] input = context.getInput();

        MessageReceivedEvent event = context.getEvent();
        arguments.add(new CommandEvent(event.getJDA(), event.getResponseNumber(), event.getMessage(), command, null));
        for (int i = 0; i < input.length; i++) {
            // + 1 so we skip the CommandEvent
            ParameterDefinition parameter = command.getParameters().get(i + 1);
            String raw = input[i];

            Optional<ParameterAdapter<?>> adapter = adapterRegistry.get(parameter.getType());
            if (!adapter.isPresent()) {
                return;
            }

            Optional<?> parsed = adapter.get().parse(raw, context);
            if (!parsed.isPresent()) {
                return;
            }
            arguments.add(parsed.get());
        }
        context.setArguments(arguments);
//        for (Filter filter : filterRegistry.getAll()) {
//            filter.apply(context);
//            if (context.isCancelled()) {
//                return;
//            }
//        }

        try {
            command.getMethod().invoke(command.getInstance(), arguments.toArray());
        } catch (Exception e) {
            throw new CommandException("Command execution failed!", e);
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
}
