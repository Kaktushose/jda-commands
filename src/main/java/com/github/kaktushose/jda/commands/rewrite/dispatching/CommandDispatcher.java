package com.github.kaktushose.jda.commands.rewrite.dispatching;

import com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.ParameterAdapterRegistry;
import com.github.kaktushose.jda.commands.rewrite.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.filter.FilterRegistry;
import com.github.kaktushose.jda.commands.rewrite.dispatching.parser.ParserSupervisor;
import com.github.kaktushose.jda.commands.rewrite.dispatching.parser.impl.MessageParser;
import com.github.kaktushose.jda.commands.rewrite.dispatching.router.CommandRouter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.router.Router;
import com.github.kaktushose.jda.commands.rewrite.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.rewrite.reflect.CommandDefinition;
import com.github.kaktushose.jda.commands.rewrite.reflect.CommandRegistry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

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
        // workaround for the moment
        if (context.getEvent().getAuthor().isBot()) {
            return;
        }

        router.findCommands(context, commandRegistry.getCommands());
        if (checkCancelled(context)) {
            log.debug("No matching command found!");
            return;
        }
        CommandDefinition command = context.getCommand();
        log.debug("Input matches command: {}", command);

        adapterRegistry.adapt(context);
        if (checkCancelled(context)) {
            return;
        }

        log.debug("Applying filters...");
        for (Filter filter : filterRegistry.getAll()) {
            filter.apply(context);
            if (checkCancelled(context)) {
                return;
            }
        }

        log.info("Executing command {} for user {}", command.getMethod().getName(), context.getEvent().getAuthor());
        try {
            log.debug("Invoking method with following arguments: {}", context.getArguments());
            command.getMethod().invoke(command.getInstance(), context.getArguments().toArray());
        } catch (Exception e) {
            log.error("Command execution failed!", new InvocationTargetException(e));
        }
    }

    private boolean checkCancelled(CommandContext context) {
        if (context.isCancelled()) {
            context.getEvent().getChannel().sendMessage(context.getErrorMessage()).queue();
            return true;
        }
        return false;
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
