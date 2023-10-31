package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.DispatcherSupervisor;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.filter.FilterRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an active instance of this framework and provides access to all underlying classes.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 1.0.0
 */
public class JDACommands {

    private static final Logger log = LoggerFactory.getLogger(JDACommands.class);
    private static boolean isActive;
    private final JDAContext jdaContext;
    private final ImplementationRegistry implementationRegistry;
    private final DispatcherSupervisor dispatcherSupervisor;
    private final FilterRegistry filterRegistry;
    private final TypeAdapterRegistry adapterRegistry;
    private final ValidatorRegistry validatorRegistry;
    private final DependencyInjector dependencyInjector;
    private final InteractionRegistry interactionRegistry;
    private final SlashCommandUpdater updater;
    private final RuntimeSupervisor runtimeSupervisor;

    // this is needed for unit testing
    protected JDACommands() {
        jdaContext = null;
        implementationRegistry = null;
        runtimeSupervisor = null;
        filterRegistry = null;
        adapterRegistry = null;
        validatorRegistry = null;
        dependencyInjector = null;
        dispatcherSupervisor = null;
        interactionRegistry = null;
        updater = null;
    }

    private JDACommands(Object jda, Class<?> clazz, LocalizationFunction function, String... packages) {
        log.info("Starting JDA-Commands...");

        if (isActive) {
            throw new IllegalStateException("An instance of the command framework is already running!");
        }

        jdaContext = new JDAContext(jda);
        dependencyInjector = new DependencyInjector();
        dependencyInjector.index(clazz, packages);

        filterRegistry = new FilterRegistry();
        adapterRegistry = new TypeAdapterRegistry();
        validatorRegistry = new ValidatorRegistry();
        implementationRegistry = new ImplementationRegistry(
                dependencyInjector,
                filterRegistry,
                adapterRegistry,
                validatorRegistry
        );
        interactionRegistry = new InteractionRegistry(validatorRegistry, dependencyInjector, function);

        runtimeSupervisor = new RuntimeSupervisor(dependencyInjector);
        dispatcherSupervisor = new DispatcherSupervisor(this);

        implementationRegistry.index(clazz, packages);

        interactionRegistry.index(clazz, packages);

        updater = new SlashCommandUpdater(this);
        updater.updateAllCommands();
        jdaContext.performTask(it -> it.addEventListener(dispatcherSupervisor));

        isActive = true;
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
        return new JDACommands(jda, clazz, ResourceBundleLocalizationFunction.empty().build(), packages);
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
        return new JDACommands(shardManager, clazz, ResourceBundleLocalizationFunction.empty().build(), packages);
    }

    /**
     * Creates a new JDACommands instance and starts the frameworks.
     *
     * @param jda      the corresponding {@link JDA} instance
     * @param clazz    a class of the classpath to scan
     * @param function the {@link LocalizationFunction} to use
     * @param packages package(s) to exclusively scan
     * @return a new JDACommands instance
     */
    public static JDACommands start(@NotNull JDA jda, @NotNull Class<?> clazz, LocalizationFunction function, @NotNull String... packages) {
        return new JDACommands(jda, clazz, function, packages);
    }

    /**
     * Creates a new JDACommands instance and starts the frameworks.
     *
     * @param shardManager the corresponding {@link ShardManager} instance
     * @param clazz        a class of the classpath to scan
     * @param function     the {@link LocalizationFunction} to use
     * @param packages     package(s) to exclusively scan
     * @return a new JDACommands instance
     */
    public static JDACommands start(@NotNull ShardManager shardManager, @NotNull Class<?> clazz, LocalizationFunction function, @NotNull String... packages) {
        return new JDACommands(shardManager, clazz, function, packages);
    }

    /**
     * Whether this JDACommands instance is active.
     *
     * @return {@code true} if the JDACommands instance is active
     */
    public static boolean isActive() {
        return isActive;
    }

    /**
     * Shuts down this JDACommands instance, making it unable to receive any events from Discord.
     * This will <b>not</b> unregister any slash commands.
     */
    public void shutdown() {
        jdaContext.performTask(jda -> jda.removeEventListener(dispatcherSupervisor));
        isActive = false;
    }

    /**
     * Updates all slash commands that are registered with
     * {@link com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand.CommandScope#GUILD
     * CommandScope#Guild}
     *
     * @return this instance
     */
    public JDACommands updateGuildCommands() {
        updater.updateGuildCommands();
        return this;
    }

    /**
     * Gets the {@link ImplementationRegistry}.
     *
     * @return the {@link ImplementationRegistry}
     */
    public ImplementationRegistry getImplementationRegistry() {
        return implementationRegistry;
    }


    /**
     * Gets the {@link RuntimeSupervisor}
     *
     * @return the {@link RuntimeSupervisor}
     */
    public RuntimeSupervisor getRuntimeSupervisor() {
        return runtimeSupervisor;
    }

    /**
     * Gets the {@link TypeAdapterRegistry}.
     *
     * @return the {@link TypeAdapterRegistry}
     */
    public TypeAdapterRegistry getAdapterRegistry() {
        return adapterRegistry;
    }

    /**
     * Gets the {@link ValidatorRegistry}.
     *
     * @return the {@link ValidatorRegistry}
     */
    public ValidatorRegistry getValidatorRegistry() {
        return validatorRegistry;
    }

    /**
     * Gets the {@link InteractionRegistry}.
     *
     * @return the {@link InteractionRegistry}
     */
    public InteractionRegistry getInteractionRegistry() {
        return interactionRegistry;
    }

    /**
     * Gets the {@link JDAContext}.
     *
     * @return the JDAContext.
     */
    public JDAContext getJdaContext() {
        return jdaContext;
    }

    /**
     * Gets the {@link FilterRegistry}.
     *
     * @return the {@link FilterRegistry}
     */
    public FilterRegistry getFilterRegistry() {
        return filterRegistry;
    }

    /**
     * Gets the {@link DependencyInjector}.
     *
     * @return the {@link DependencyInjector}
     */
    public DependencyInjector getDependencyInjector() {
        return dependencyInjector;
    }
}
