package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu;
import com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu;
import com.github.kaktushose.jda.commands.dependency.DefaultDependencyInjector;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.internal.JDAEventListener;
import com.github.kaktushose.jda.commands.dispatching.internal.Runtime;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.handling.DispatchingContext;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.ConstraintMiddleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.CooldownMiddleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.PermissionsMiddleware;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.internal.JDAContext;
import com.github.kaktushose.jda.commands.internal.register.SlashCommandUpdater;
import com.github.kaktushose.jda.commands.definitions.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.definitions.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.components.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.components.menus.GenericSelectMenuDefinition;
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

    private static JDACommands startInternal(Object jda, Class<?> clazz, LocalizationFunction function, DependencyInjector dependencyInjector, ExpirationStrategy expirationStrategy, String[] packages) {
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

        var eventListener = new JDAEventListener(new DispatchingContext(middlewareRegistry, implementationRegistry, interactionRegistry, adapterRegistry, expirationStrategy));

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
        return startInternal(jda, clazz, ResourceBundleLocalizationFunction.empty().build(), new DefaultDependencyInjector(), ExpirationStrategy.AFTER_15_MINUTES, packages);
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
        return startInternal(shardManager, clazz, ResourceBundleLocalizationFunction.empty().build(), new DefaultDependencyInjector(), ExpirationStrategy.AFTER_15_MINUTES, packages);
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
        return startInternal(jda, clazz, function, new DefaultDependencyInjector(), ExpirationStrategy.AFTER_15_MINUTES, packages);
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
        return startInternal(shardManager, clazz, function, new DefaultDependencyInjector(), ExpirationStrategy.AFTER_15_MINUTES, packages);
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
    public static JDACommands start(@NotNull JDA jda, @NotNull Class<?> clazz, LocalizationFunction function, DependencyInjector injector, ExpirationStrategy expirationStrategy, @NotNull String... packages) {
        return startInternal(jda, clazz, function, injector, expirationStrategy, packages);
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
    public static JDACommands start(@NotNull ShardManager shardManager, @NotNull Class<?> clazz, LocalizationFunction function, DependencyInjector injector, ExpirationStrategy expirationStrategy, @NotNull String... packages) {
        return startInternal(shardManager, clazz, function, injector, expirationStrategy, packages);
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

    /// Gets a [`Button`][com.github.kaktushose.jda.commands.annotations.interactions.Button] based on the method name
    /// and transforms it into a JDA [Button].
    ///
    /// The button will be [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) independent. This may be useful if you want to send a message without
    /// using the framework.
    ///
    /// @param button the name of the button
    /// @return the JDA [Button]
    @NotNull
    public Button getButton(@NotNull String button) {
        if (!button.matches("[a-zA-Z]+\\.[a-zA-Z]+")) {
            throw new IllegalArgumentException("Unknown Button");
        }

        String sanitizedId = button.replaceAll("\\.", "");
        ButtonDefinition buttonDefinition = interactionRegistry.getButtons().stream()
                .filter(it -> it.getDefinitionId().equals(sanitizedId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Button"));

        return buttonDefinition.toButton().withId(buttonDefinition.independentCustomId());
    }

    /// Gets a [StringSelectMenu] or [EntitySelectMenu] based on the method name and transforms it into a JDA [SelectMenu].
    ///
    /// The select menu will be [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) independent. This may be useful if you want to send a component
    /// without using the framework.
    ///
    /// @param <S>  the type of [SelectMenu]
    /// @param menu the name of the select menu
    /// @return the JDA [SelectMenu]
    @SuppressWarnings("unchecked")
    @NotNull
    public <S extends SelectMenu> S getSelectMenu(@NotNull String menu) {
        if (!menu.matches("[a-zA-Z]+\\.[a-zA-Z]+")) {
            throw new IllegalArgumentException("Unknown Select Menu");
        }

        String sanitizedId = menu.replaceAll("\\.", "");
        GenericSelectMenuDefinition<?> selectMenuDefinition = interactionRegistry.getSelectMenus().stream()
                .filter(it -> it.getDefinitionId().equals(sanitizedId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Select Menu"));

        return (S) selectMenuDefinition.toSelectMenu(selectMenuDefinition.independentCustomId(), true);
    }
}
