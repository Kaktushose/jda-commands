package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.CommandDispatcher;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.filter.FilterRegistry;
import com.github.kaktushose.jda.commands.dispatching.parser.ParserSupervisor;
import com.github.kaktushose.jda.commands.dispatching.router.Router;
import com.github.kaktushose.jda.commands.dispatching.slash.SlashConfiguration;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import com.github.kaktushose.jda.commands.reflect.CommandRegistry;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Represents an active instance of this framework and provides access to all underlying classes. This is basically
 * an abstraction of the {@link CommandDispatcher}.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 1.0.0
 */
public class JDACommands {

    private static final Logger log = LoggerFactory.getLogger(JDACommands.class);
    private final CommandDispatcher commandDispatcher;

    private JDACommands(Object jda, Class<?> clazz, String... packages) {
        this(jda, clazz, null, packages);
    }

    JDACommands(Object jda, Class<?> clazz, SlashConfiguration configuration, String... packages) {
        log.info("Starting JDA-Commands...");
        this.commandDispatcher = new CommandDispatcher(new JDAContext(jda), this, clazz, configuration, packages);
        log.info("Finished loading!");
    }

    /**
     * Creates a new JDACommands instance and starts the frameworks.
     *
     * @param jda      the corresponding {@link JDA} instance
     * @param clazz    a class of the classpath to scan
     * @param packages package(s) to exclusively scan
     * @return a new JDACommands instance
     */
    public static JDACommands start(@NotNull JDA jda, @NotNull Class<?> clazz, @NotNull String... packages) {
        return new JDACommands(jda, clazz, packages);
    }

    /**
     * Creates a new JDACommands instance and starts the frameworks.
     *
     * @param shardManager the corresponding {@link ShardManager} instance
     * @param clazz        a class of the classpath to scan
     * @param packages     package(s) to exclusively scan
     * @return a new JDACommands instance
     */
    public static JDACommands start(@NotNull ShardManager shardManager, @NotNull Class<?> clazz, @NotNull String... packages) {
        return new JDACommands(shardManager, clazz, packages);
    }

    /**
     * Returns a new {@link JDACommandsSlashBuilder) instance.
     *
     * @param jda      the corresponding {@link JDA} instance
     * @param clazz    a class of the classpath to scan
     * @param packages package(s) to exclusively scan
     * @return a new JDACommands instance
     */
    public static JDACommandsSlashBuilder slash(@NotNull JDA jda, @NotNull Class<?> clazz, @NotNull String... packages) {
        return new JDACommandsSlashBuilder(jda, clazz, packages);
    }

    /**
     * Returns a new {@link JDACommandsSlashBuilder) instance.
     *
     * @param jda      the corresponding {@link JDA} instance
     * @param clazz    a class of the classpath to scan
     * @param packages package(s) to exclusively scan
     * @return a new JDACommands instance
     */
    public static JDACommandsSlashBuilder slash(@NotNull ShardManager jda, @NotNull Class<?> clazz, @NotNull String... packages) {
        return new JDACommandsSlashBuilder(jda, clazz, packages);
    }

    /**
     * Shuts down this JDACommands instance making it unable to receive any events from Discord.
     * This will <b>not</b> unregister any slash commands.
     */
    public void shutdown() {
        commandDispatcher.shutdown();
        log.info("Finished shutdown!");
    }

    /**
     * Gets the {@link ImplementationRegistry}.
     *
     * @return the {@link ImplementationRegistry}
     */
    public ImplementationRegistry getImplementationRegistry() {
        return commandDispatcher.getImplementationRegistry();
    }

    /**
     * Gets the {@link ParserSupervisor}.
     *
     * @return the {@link ParserSupervisor}
     */
    public ParserSupervisor getParserSupervisor() {
        return commandDispatcher.getParserSupervisor();
    }

    /**
     * Gets the {@link TypeAdapterRegistry}.
     *
     * @return the {@link TypeAdapterRegistry}
     */
    public TypeAdapterRegistry getAdapterRegistry() {
        return commandDispatcher.getAdapterRegistry();
    }

    /**
     * Gets the {@link FilterRegistry}.
     *
     * @return the {@link FilterRegistry}
     */
    public FilterRegistry getFilterRegistry() {
        return commandDispatcher.getFilterRegistry();
    }

    /**
     * Gets the {@link ValidatorRegistry}.
     *
     * @return the {@link ValidatorRegistry}
     */
    public ValidatorRegistry getValidatorRegistry() {
        return commandDispatcher.getValidatorRegistry();
    }

    /**
     * Gets the {@link CommandRegistry}.
     *
     * @return the {@link CommandRegistry}
     */
    public CommandRegistry getCommandRegistry() {
        return commandDispatcher.getCommandRegistry();
    }

    /**
     * Gets the {@link DependencyInjector}.
     *
     * @return the {@link DependencyInjector}
     */
    public DependencyInjector getDependencyInjector() {
        return commandDispatcher.getDependencyInjector();
    }

    /**
     * Gets the {@link Router}.
     *
     * @return the {@link Router}
     * @deprecated use {@link ImplementationRegistry#getRouter}
     */
    public Router getRouter() {
        return commandDispatcher.getRouter();
    }

    /**
     * Sets the {@link Router} to use.
     *
     * @param router the new {@link Router} to use
     * @return this JDACommands instance
     * @deprecated use {@link ImplementationRegistry#setRouter(Router)}
     */
    public JDACommands setRouter(@NotNull Router router) {
        commandDispatcher.setRouter(router);
        return this;
    }

    /**
     * Gets all active commands.
     *
     * @return a set of all active commands
     */
    public Set<CommandDefinition> getCommands() {
        return commandDispatcher.getCommandRegistry().getCommands();
    }
}
