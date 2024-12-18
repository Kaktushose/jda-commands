package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.dependency.DefaultDependencyInjector;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.JDAEventListener;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.handling.HandlerContext;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.ConstraintMiddleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.CooldownMiddleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.PermissionsMiddleware;
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


public record JDACommands(
        JDAContext jdaContext,
        JDAEventListener JDAEventListener,
        MiddlewareRegistry middlewareRegistry,
        TypeAdapterRegistry adapterRegistry,
        ValidatorRegistry validatorRegistry,
        DependencyInjector dependencyInjector,
        InteractionRegistry interactionRegistry,
        SlashCommandUpdater updater) {
    private static final Logger log = LoggerFactory.getLogger(JDACommands.class);

    private static JDACommands startInternal(Object jda, Class<?> clazz, LocalizationFunction function, DependencyInjector dependencyInjector, String... packages) {
        log.info("Starting JDA-Commands...");

        var jdaContext = new JDAContext(jda);
        dependencyInjector.index(clazz, packages);

        var middlewareRegistry = new MiddlewareRegistry();
        var adapterRegistry = new TypeAdapterRegistry();
        var validatorRegistry = new ValidatorRegistry();
        var implementationRegistry = new ImplementationRegistry(dependencyInjector, middlewareRegistry, adapterRegistry, validatorRegistry);
        var interactionRegistry = new InteractionRegistry(validatorRegistry, dependencyInjector, function);

        middlewareRegistry.register(Priority.PERMISSIONS, new PermissionsMiddleware(implementationRegistry));
        middlewareRegistry.register(Priority.NORMAL, new ConstraintMiddleware(implementationRegistry), new CooldownMiddleware(implementationRegistry));

        var eventListener = new JDAEventListener(new HandlerContext(middlewareRegistry, implementationRegistry, interactionRegistry, adapterRegistry));

        implementationRegistry.index(clazz, packages);

        interactionRegistry.index(clazz, packages);

        var updater = new SlashCommandUpdater(jdaContext, implementationRegistry.getGuildScopeProvider(), interactionRegistry);
        updater.updateAllCommands();

        jdaContext.performTask(it -> it.addEventListener(eventListener));

        log.info("Finished loading!");

        return new JDACommands(
                jdaContext,
                eventListener,
                middlewareRegistry,
                adapterRegistry,
                validatorRegistry,
                dependencyInjector,
                interactionRegistry,
                updater
        );
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
        return startInternal(jda, clazz, ResourceBundleLocalizationFunction.empty().build(), new DefaultDependencyInjector(), packages);
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
        return startInternal(shardManager, clazz, ResourceBundleLocalizationFunction.empty().build(), new DefaultDependencyInjector(), packages);
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
        return startInternal(jda, clazz, function, new DefaultDependencyInjector(), packages);
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
        return startInternal(shardManager, clazz, function, new DefaultDependencyInjector(), packages);
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
        return startInternal(jda, clazz, function, injector, packages);
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
        return startInternal(shardManager, clazz, function, injector, packages);
    }

    /**
     * Shuts down this JDACommands instance, making it unable to receive any events from Discord.
     * This will <b>not</b> unregister any slash commands.
     */
    public void shutdown() {
        jdaContext.performTask(jda -> jda.removeEventListener(JDAEventListener));
    }

    /**
     * Updates all slash commands that are registered with
     * {@link com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand.CommandScope#GUILD
     * CommandScope#Guild}
     */
    public void updateGuildCommands() {
        updater.updateGuildCommands();
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
//        if (!button.matches("[a-zA-Z]+\\.[a-zA-Z]+")) {
//            throw new IllegalArgumentException("Unknown Button");
//        }
//
//        String sanitizedId = button.replaceAll("\\.", "");
//        ButtonDefinition buttonDefinition = interactionRegistry.getButtons().stream()
//                .filter(it -> it.getDefinitionId().equals(sanitizedId))
//                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Button"));
//
//        RuntimeSupervisor.InteractionRuntime runtime = runtimeSupervisor.newRuntime(buttonDefinition);
//        return buttonDefinition.toButton().withId(buttonDefinition.createCustomId(runtime.getRuntimeId()));
        return null;
    }

    /**
     * Gets a JDA {@link Button} to use it for message builders based on the jda-commands id and links it an
     * existing
     * {@link com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor.InteractionRuntime InteractionRuntime}.
     *
     *
     * <p>
     * The id is made up of the simple class name and the method name. E.g. the id of a button defined by a
     * {@code onButton(ComponentEvent event)} method inside an {@code ExampleButton} class would be
     * {@code ExampleButton.onButton}.
     * </p>
     *
     * @param button    the id of the button
     * @param runtimeId the id of the
     *                  {@link com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor.InteractionRuntime InteractionRuntime}
     * @return a JDA {@link Button}
     */
    public Button getButton(String button, String runtimeId) {
        if (!button.matches("[a-zA-Z]+\\.[a-zA-Z]+")) {
            throw new IllegalArgumentException("Unknown Button");
        }

        String sanitizedId = button.replaceAll("\\.", "");
        ButtonDefinition buttonDefinition = interactionRegistry.getButtons().stream()
                .filter(it -> it.getDefinitionId().equals(sanitizedId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Button"));

        return buttonDefinition.toButton().withId(buttonDefinition.scopedCustomId(runtimeId));
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
    @SuppressWarnings("unchecked")
    public <T extends SelectMenu> T getSelectMenu(String selectMenu) {
//        if (!selectMenu.matches("[a-zA-Z]+\\.[a-zA-Z]+")) {
//            throw new IllegalArgumentException("Unknown Select Menu");
//        }
//
//        String sanitizedId = selectMenu.replaceAll("\\.", "");
//        GenericSelectMenuDefinition<?> selectMenuDefinition = interactionRegistry.getSelectMenus().stream()
//                .filter(it -> it.getDefinitionId().equals(sanitizedId))
//                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Select Menu"));
//
//        RuntimeSupervisor.InteractionRuntime runtime = runtimeSupervisor.newRuntime(selectMenuDefinition);
//        return (T) selectMenuDefinition.toSelectMenu(runtime.getRuntimeId(), true);
        return null;
    }

    /**
     * Gets a JDA {@link SelectMenu} subtype to use it for message builders based on the jda-commands id and links it an
     * existing
     * {@link com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor.InteractionRuntime InteractionRuntime}.
     *
     * <p>
     * The id is made up of the simple class name and the method name. E.g. the id of a select menu defined by a
     * {@code onSelectMenu(ComponentEvent event)} method inside an {@code ExampleMenu} class would be
     * {@code ExampleMenu.onSelectMenu}.
     * </p>
     *
     * @param selectMenu the id of the selectMenu
     * @param runtimeId  the id of the
     *                   {@link com.github.kaktushose.jda.commands.dispatching.RuntimeSupervisor.InteractionRuntime
     *                   InteractionRuntime}
     * @return a JDA {@link SelectMenu}
     */
    @SuppressWarnings("unchecked")
    public <T extends SelectMenu> T getSelectMenu(String selectMenu, String runtimeId) {
        if (!selectMenu.matches("[a-zA-Z]+\\.[a-zA-Z]+")) {
            throw new IllegalArgumentException("Unknown Select Menu");
        }

        String sanitizedId = selectMenu.replaceAll("\\.", "");
        GenericSelectMenuDefinition<?> selectMenuDefinition = interactionRegistry.getSelectMenus().stream()
                .filter(it -> it.getDefinitionId().equals(sanitizedId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Select Menu"));

        return (T) selectMenuDefinition.toSelectMenu(runtimeId, true);
    }
}
