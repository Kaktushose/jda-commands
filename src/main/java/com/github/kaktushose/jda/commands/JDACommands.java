package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.dependency.DefaultDependencyInjector;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.DispatcherSupervisor;
import com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.components.ButtonDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.components.menus.GenericSelectMenuDefinition;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an active instance of this framework and provides access to all underlying classes.
 *
 * @since 1.0.0
 */
public class JDACommands {

    private static final Logger log = LoggerFactory.getLogger(JDACommands.class);
    private static boolean isActive;
    private final JDAContext jdaContext;
    private final ImplementationRegistry implementationRegistry;
    private final DispatcherSupervisor dispatcherSupervisor;
    private final MiddlewareRegistry middlewareRegistry;
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
        middlewareRegistry = null;
        adapterRegistry = null;
        validatorRegistry = null;
        dependencyInjector = null;
        dispatcherSupervisor = null;
        interactionRegistry = null;
        updater = null;
    }

    private JDACommands(Object jda, Class<?> clazz, LocalizationFunction function, DependencyInjector injector, String... packages) {
        log.info("Starting JDA-Commands...");

        if (isActive) {
            throw new IllegalStateException("An instance of the command framework is already running!");
        }

        jdaContext = new JDAContext(jda);
        dependencyInjector = injector;
        dependencyInjector.index(clazz, packages);

        middlewareRegistry = new MiddlewareRegistry();
        adapterRegistry = new TypeAdapterRegistry();
        validatorRegistry = new ValidatorRegistry();
        implementationRegistry = new ImplementationRegistry(dependencyInjector, middlewareRegistry, adapterRegistry, validatorRegistry);
        interactionRegistry = new InteractionRegistry(validatorRegistry, dependencyInjector, function);

        runtimeSupervisor = new RuntimeSupervisor(dependencyInjector);
        dispatcherSupervisor = new DispatcherSupervisor(this);

        implementationRegistry.index(clazz, packages);

        interactionRegistry.index(clazz, packages);

        updater = new SlashCommandUpdater(this);
        updater.updateGlobalCommands();
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
        return new JDACommands(jda, clazz, ResourceBundleLocalizationFunction.empty().build(), new DefaultDependencyInjector(), packages);
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
        return new JDACommands(shardManager, clazz, ResourceBundleLocalizationFunction.empty().build(), new DefaultDependencyInjector(), packages);
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
        return new JDACommands(jda, clazz, function, new DefaultDependencyInjector(), packages);
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
        return new JDACommands(shardManager, clazz, function, new DefaultDependencyInjector(), packages);
    }

    /**
     * Creates a new JDACommands instance and starts the frameworks.
     *
     * @param jda      the corresponding {@link JDA} instance
     * @param clazz    a class of the classpath to scan
     * @param function the {@link LocalizationFunction} to use
     * @param injector the {@link DependencyInjector} implementation to use
     * @param packages package(s) to exclusively scan
     * @return a new JDACommands instance
     */
    public static JDACommands start(@NotNull JDA jda, @NotNull Class<?> clazz, LocalizationFunction function, DependencyInjector injector, @NotNull String... packages) {
        return new JDACommands(jda, clazz, function, injector, packages);
    }

    /**
     * Creates a new JDACommands instance and starts the frameworks.
     *
     * @param shardManager the corresponding {@link ShardManager} instance
     * @param clazz        a class of the classpath to scan
     * @param function     the {@link LocalizationFunction} to use
     * @param injector     the {@link DependencyInjector} implementation to use
     * @param packages     package(s) to exclusively scan
     * @return a new JDACommands instance
     */
    public static JDACommands start(@NotNull ShardManager shardManager, @NotNull Class<?> clazz, LocalizationFunction function, DependencyInjector injector, @NotNull String... packages) {
        return new JDACommands(shardManager, clazz, function, injector, packages);
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
     * Gets the {@link DispatcherSupervisor}.
     *
     * @return the {@link DispatcherSupervisor}
     */
    public DispatcherSupervisor getDispatcherSupervisor() {
        return dispatcherSupervisor;
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
     * Gets a JDA {@link Button} to use it for message builders based on the jda-commands id.
     *
     * <p>
     * The id is made up of the simple class name and the method name. E.g. the id of a button defined by a
     * {@code onButton(ComponentEvent event)} method inside an {@code ExampleButton} class would be
     * {@code ExampleButton.onButton}.
     * </p>
     *
     * @param button the id of the button
     * @return a JDA {@link Button}
     */
    public Button getButton(String button) {
        if (!button.matches("[a-zA-Z]+\\.[a-zA-Z]+")) {
            throw new IllegalArgumentException("Unknown Button");
        }

        String sanitizedId = button.replaceAll("\\.", "");
        ButtonDefinition buttonDefinition = interactionRegistry.getButtons().stream()
                .filter(it -> it.getDefinitionId().equals(sanitizedId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Button"));

        RuntimeSupervisor.InteractionRuntime runtime = runtimeSupervisor.newRuntime(buttonDefinition);
        return buttonDefinition.toButton().withId(buttonDefinition.createCustomId(runtime.getRuntimeId()));
    }

    /**
     * Gets a JDA {@link SelectMenu} to use it for message builders based on the jda-commands id.
     *
     * <p>
     * The id is made up of the simple class name and the method name. E.g. the id of a a select menu defined by a
     * {@code onSelectMenu(ComponentEvent event)} method inside an {@code ExampleMenu} class would be
     * {@code ExampleMenu.onSelectMenu}.
     * </p>
     *
     * @param selectMenu the id of the selectMenu
     * @return a JDA {@link SelectMenu}
     */
    public SelectMenu getSelectMenu(String selectMenu) {
        if (!selectMenu.matches("[a-zA-Z]+\\.[a-zA-Z]+")) {
            throw new IllegalArgumentException("Unknown Select Menu");
        }

        String sanitizedId = selectMenu.replaceAll("\\.", "");
        GenericSelectMenuDefinition<?> selectMenuDefinition = interactionRegistry.getSelectMenus().stream()
                .filter(it -> it.getDefinitionId().equals(sanitizedId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Select Menu"));

        RuntimeSupervisor.InteractionRuntime runtime = runtimeSupervisor.newRuntime(selectMenuDefinition);
        return selectMenuDefinition.toSelectMenu(runtime.getRuntimeId(), true);
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
    public JDAContext getJDAContext() {
        return jdaContext;
    }

    /**
     * Gets the {@link MiddlewareRegistry}.
     *
     * @return the {@link MiddlewareRegistry}
     */
    public MiddlewareRegistry getMiddlewareRegistry() {
        return middlewareRegistry;
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
