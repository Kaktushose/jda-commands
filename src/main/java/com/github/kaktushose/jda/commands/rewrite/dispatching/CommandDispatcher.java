package com.github.kaktushose.jda.commands.rewrite.dispatching;

import com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.ParameterAdapterRegistry;
import com.github.kaktushose.jda.commands.rewrite.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.filter.FilterRegistry;
import com.github.kaktushose.jda.commands.rewrite.dispatching.parser.ParserSupervisor;
import com.github.kaktushose.jda.commands.rewrite.dispatching.parser.impl.MessageParser;
import com.github.kaktushose.jda.commands.rewrite.dispatching.router.CommandRouter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.router.Router;
import com.github.kaktushose.jda.commands.rewrite.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.rewrite.reflect.CommandRegistry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

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

        for (Filter filter : filterRegistry.getAll()) {
            filter.apply(context);
            if (context.isCancelled()) {
                return;
            }
        }

        // TODO parameter adapting

        // TODO command execution
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
