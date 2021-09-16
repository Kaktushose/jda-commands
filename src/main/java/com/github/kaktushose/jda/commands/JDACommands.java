package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.dispatching.CommandDispatcher;
import com.github.kaktushose.jda.commands.dispatching.adapter.ParameterAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.filter.FilterRegistry;
import com.github.kaktushose.jda.commands.dispatching.parser.ParserSupervisor;
import com.github.kaktushose.jda.commands.dispatching.router.Router;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.CommandRegistry;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDACommands {

    private static final Logger log = LoggerFactory.getLogger(JDACommands.class);
    private final CommandDispatcher commandDispatcher;

    private JDACommands(Object jda, boolean isShardManager, String... packages) {
        log.info("Starting JDA-Commands...");
        this.commandDispatcher = new CommandDispatcher(jda, isShardManager, packages);
        log.info("Finished loading!");
    }

    public static JDACommands start(JDA jda, String... packages) {
        return new JDACommands(jda, false, packages);
    }

    public static JDACommands start(ShardManager shardManager, String... packages) {
        return new JDACommands(shardManager, true, packages);
    }

    public void shutdown() {
        commandDispatcher.shutdown();
        log.info("Finished shutdown!");
    }

    public ImplementationRegistry getImplementationRegistry() {
        return commandDispatcher.getImplementationRegistry();
    }

    public ParserSupervisor getParserSupervisor() {
        return commandDispatcher.getParserSupervisor();
    }

    public ParameterAdapterRegistry getAdapterRegistry() {
        return commandDispatcher.getAdapterRegistry();
    }

    public FilterRegistry getFilterRegistry() {
        return commandDispatcher.getFilterRegistry();
    }

    public ValidatorRegistry getValidatorRegistry() {
        return commandDispatcher.getValidatorRegistry();
    }

    public CommandRegistry getCommandRegistry() {
        return commandDispatcher.getCommandRegistry();
    }

    public Router getRouter() {
        return commandDispatcher.getRouter();
    }

    public JDACommands setRouter(Router router) {
        commandDispatcher.setRouter(router);
        return this;
    }
}