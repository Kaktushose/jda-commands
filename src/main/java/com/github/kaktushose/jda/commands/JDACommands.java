package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu;
import com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu;
import com.github.kaktushose.jda.commands.definitions.description.reflective.ReflectiveClassFinder;
import com.github.kaktushose.jda.commands.definitions.description.reflective.ReflectiveDescriptor;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.SelectMenuDefinition;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.ImplementationRegistry;
import com.github.kaktushose.jda.commands.dispatching.JDAEventListener;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.expiration.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.handling.DispatchingContext;
import com.github.kaktushose.jda.commands.dispatching.middleware.Priority;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.ConstraintMiddleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.CooldownMiddleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.impl.PermissionsMiddleware;
import com.github.kaktushose.jda.commands.dispatching.middleware.internal.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.ValidatorRegistry;
import com.github.kaktushose.jda.commands.internal.JDAContext;
import com.github.kaktushose.jda.commands.internal.register.SlashCommandUpdater;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class JDACommands {
    private static final Logger log = LoggerFactory.getLogger(JDACommands.class);
    private final JDAContext jdaContext;
    private final com.github.kaktushose.jda.commands.dispatching.JDAEventListener JDAEventListener;
    private final MiddlewareRegistry middlewareRegistry = new MiddlewareRegistry();
    private final TypeAdapterRegistry adapterRegistry = new TypeAdapterRegistry();
    private final ValidatorRegistry validatorRegistry = new ValidatorRegistry();
    private final DependencyInjector dependencyInjector;
    private final InteractionRegistry interactionRegistry;
    private final SlashCommandUpdater updater;
    private final ImplementationRegistry implementationRegistry;

    JDACommands(
            JDAContext jdaContext,
            DependencyInjector dependencyInjector,
            LocalizationFunction localizationFunction,
            ExpirationStrategy expirationStrategy) {
        this.jdaContext = jdaContext;
        this.dependencyInjector = dependencyInjector;
        this.interactionRegistry = new InteractionRegistry(dependencyInjector, validatorRegistry, localizationFunction, new ReflectiveDescriptor());
        this.implementationRegistry = new ImplementationRegistry(dependencyInjector, middlewareRegistry, adapterRegistry, validatorRegistry);
        this.JDAEventListener = new JDAEventListener(new DispatchingContext(middlewareRegistry, implementationRegistry, interactionRegistry, adapterRegistry, expirationStrategy));
        this.updater = new SlashCommandUpdater(jdaContext, implementationRegistry.getGuildScopeProvider(), interactionRegistry);

        middlewareRegistry.register(Priority.PERMISSIONS, new PermissionsMiddleware(implementationRegistry));
        middlewareRegistry.register(Priority.NORMAL, new ConstraintMiddleware(implementationRegistry), new CooldownMiddleware(implementationRegistry));
    }

    JDACommands start(Class<?> clazz, String[] packages) {
        log.info("Starting JDA-Commands...");
        dependencyInjector.index(clazz, packages);
        implementationRegistry.index(clazz, packages);
        interactionRegistry.index(ReflectiveClassFinder.find(clazz, packages));
        updater.updateAllCommands();

        jdaContext.performTask(it -> it.addEventListener(JDAEventListener));
        log.info("Finished loading!");
        return this;
    }


    MiddlewareRegistry middlewareRegistry() {
        return middlewareRegistry;
    }

    TypeAdapterRegistry adapterRegistry() {
        return adapterRegistry;
    }

    ValidatorRegistry validatorRegistry() {
        return validatorRegistry;
    }

    ImplementationRegistry implementationRegistry() {
        return implementationRegistry;
    }

    /// Creates a new JDACommands instance and starts the frameworks.
    ///
    /// @param jda      the corresponding [JDA] instance
    /// @param clazz    a class of the classpath to scan
    /// @param packages package(s) to exclusively scan
    /// @return a new JDACommands instance
    public static JDACommands start(@NotNull JDA jda, @NotNull Class<?> clazz, @NotNull String... packages) {
        return builder(jda, clazz, packages).start();
    }

    /// Creates a new JDACommands instance and starts the frameworks.
    ///
    /// @param shardManager the corresponding [ShardManager] instance
    /// @param clazz        a class of the classpath to scan
    /// @param packages     package(s) to exclusively scan
    /// @return a new JDACommands instance
    public static JDACommands start(@NotNull ShardManager shardManager, @NotNull Class<?> clazz, @NotNull String... packages) {
        return builder(shardManager, clazz, packages).start();
    }

    /// Create a new builder.
    /// @param jda      the corresponding [JDA] instance
    /// @param clazz    a class of the classpath to scan
    /// @param packages package(s) to exclusively scan
    /// @return a new [JDACommandsBuilder]
    public static JDACommandsBuilder builder(JDA jda, Class<?> clazz, String... packages) {
        return new JDACommandsBuilder(new JDAContext(jda), clazz, packages);
    }

    /// Create a new builder.
    /// @param shardManager      the corresponding [ShardManager] instance
    /// @param clazz    a class of the classpath to scan
    /// @param packages package(s) to exclusively scan
    /// @return a new [JDACommandsBuilder]
    public static JDACommandsBuilder builder(ShardManager shardManager, Class<?> clazz, String... packages) {
        return new JDACommandsBuilder(new JDAContext(shardManager), clazz, packages);
    }

    /**
     * Shuts down this JDACommands instance, making it unable to receive any events from Discord.
     * This will <b>not</b> unregister any slash commands.
     */
    public void shutdown() {
        jdaContext.performTask(jda -> jda.removeEventListener(JDAEventListener));
    }

    /// Updates all slash commands that are registered with
    /// [CommandScope#Guild][#GUILD]
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
        ButtonDefinition buttonDefinition = interactionRegistry.find(ButtonDefinition.class, it -> it.definitionId().equals(sanitizedId))
                .stream()
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Button"));

        return buttonDefinition.toJDAEntity();
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
        SelectMenuDefinition<?> selectMenuDefinition = interactionRegistry.find(SelectMenuDefinition.class, it -> it.definitionId().equals(sanitizedId))
                .stream()
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown Select Menu"));

        return (S) selectMenuDefinition.toJDAEntity();
    }

}
