package com.github.kaktushose.jda.commands.dispatching;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.dispatching.filter.FilterRegistry;
import com.github.kaktushose.jda.commands.dispatching.filter.FilterRegistry.FilterPosition;
import com.github.kaktushose.jda.commands.dispatching.parser.ParserSupervisor;
import com.github.kaktushose.jda.commands.dispatching.router.CommandRouter;
import com.github.kaktushose.jda.commands.dispatching.router.Router;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.embeds.help.HelpMessageFactory;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import com.github.kaktushose.jda.commands.reflect.CommandRegistry;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class CommandDispatcher {

    private static final Logger log = LoggerFactory.getLogger(CommandDispatcher.class);
    private static boolean isActive;
    private final Object jda;
    private final boolean isShardManager;
    private final ImplementationRegistry implementationRegistry;
    private final HelpMessageFactory helpMessageFactory;
    private final ParserSupervisor parserSupervisor;
    private final FilterRegistry filterRegistry;
    private final TypeAdapterRegistry adapterRegistry;
    private final ValidatorRegistry validatorRegistry;
    private final CommandRegistry commandRegistry;
    private final DependencyInjector dependencyInjector;
    private final JDACommands jdaCommands;
    private Router router;

    public CommandDispatcher(Object jda, boolean isShardManager, JDACommands jdaCommands, String... packages) {
        this.jda = jda;
        this.isShardManager = isShardManager;
        this.jdaCommands = jdaCommands;

        if (isActive) {
            throw new IllegalStateException("An instance of the command framework is already running!");
        }

        dependencyInjector = new DependencyInjector();
        dependencyInjector.index(packages);

        implementationRegistry = new ImplementationRegistry(dependencyInjector);
        implementationRegistry.index(packages);

        helpMessageFactory = implementationRegistry.getHelpMessageFactory();

        parserSupervisor = new ParserSupervisor(this);
        if (isShardManager) {
            ((ShardManager) jda).addEventListener(parserSupervisor);
        } else {
            ((JDA) jda).addEventListener(parserSupervisor);
        }

        router = new CommandRouter();
        filterRegistry = new FilterRegistry();
        adapterRegistry = new TypeAdapterRegistry();
        validatorRegistry = new ValidatorRegistry();

        commandRegistry = new CommandRegistry(adapterRegistry, validatorRegistry, dependencyInjector);
        commandRegistry.index(packages);

        dependencyInjector.inject();
        isActive = true;
    }

    public static boolean isIsActive() {
        return isActive;
    }

    public void shutdown() {
        if (isShardManager) {
            ((ShardManager) jda).removeEventListener(this);
        } else {
            ((JDA) jda).removeEventListener(this);
        }
        isActive = false;
    }

    public void onEvent(CommandContext context) {
        log.debug("Applying filters in phase BEFORE_ROUTING...");
        for (Filter filter : filterRegistry.getAll(FilterPosition.BEFORE_ROUTING)) {
            filter.apply(context);
            if (checkCancelled(context)) {
                return;
            }
        }

        router.findCommands(context, commandRegistry.getCommands());

        if (context.isCancelled() && context.isHelpEvent()) {
            log.debug("Sending generic help");
            context.getEvent().getChannel().sendMessage(helpMessageFactory.getGenericHelp(commandRegistry.getControllers(), context)).queue();
            return;
        }

        if (checkCancelled(context)) {
            log.debug("No matching command found!");
            return;
        }

        CommandDefinition command = context.getCommand();
        log.debug("Input matches command: {}", command);

        if (context.isHelpEvent()) {
            context.getEvent().getChannel().sendMessage(helpMessageFactory.getSpecificHelp(context)).queue();
            return;
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

    public ImplementationRegistry getImplementationRegistry() {
        return implementationRegistry;
    }

    public ParserSupervisor getParserSupervisor() {
        return parserSupervisor;
    }

    public TypeAdapterRegistry getAdapterRegistry() {
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

    public FilterRegistry getFilterRegistry() {
        return filterRegistry;
    }

    public Router getRouter() {
        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }

    public HelpMessageFactory getHelpMessageFactory() {
        return helpMessageFactory;
    }

    public JDACommands getJdaCommands() {
        return jdaCommands;
    }

    public DependencyInjector getDependencyInjector() {
        return dependencyInjector;
    }
}