package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu;
import com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu;
import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.SelectMenuDefinition;
import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.JDAEventListener;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import com.github.kaktushose.jda.commands.dispatching.expiration.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.handling.DispatchingContext;
import com.github.kaktushose.jda.commands.dispatching.middleware.internal.Middlewares;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.internal.JDAContext;
import com.github.kaktushose.jda.commands.internal.register.SlashCommandUpdater;
import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/// The main entry point of the JDA-Commands framework. This class includes methods to manage the overall framework
/// while running.
///
/// Instances of this class can be created by using one of the "start" or "builder" methods.
public final class JDACommands {
    private static final Logger log = LoggerFactory.getLogger(JDACommands.class);
    private final JDAContext jdaContext;
    private final com.github.kaktushose.jda.commands.dispatching.JDAEventListener JDAEventListener;
    private final DependencyInjector dependencyInjector;
    private final InteractionRegistry interactionRegistry;
    private final SlashCommandUpdater updater;

    JDACommands(
            JDAContext jdaContext,
            DependencyInjector dependencyInjector,
            ExpirationStrategy expirationStrategy,
            TypeAdapters typeAdapters,
            Middlewares middlewares,
            ErrorMessageFactory errorMessageFactory,
            GuildScopeProvider guildScopeProvider,
            InteractionRegistry interactionRegistry,
            InteractionDefinition.ReplyConfig globalReplyConfig) {
        this.jdaContext = jdaContext;
        this.dependencyInjector = dependencyInjector;
        this.interactionRegistry = interactionRegistry;
        this.updater = new SlashCommandUpdater(jdaContext, guildScopeProvider, interactionRegistry);
        this.JDAEventListener = new JDAEventListener(new DispatchingContext(middlewares, errorMessageFactory, interactionRegistry, typeAdapters, expirationStrategy, dependencyInjector, globalReplyConfig));
    }

    JDACommands start(Collection<ClassFinder> classFinders, Class<?> clazz, String[] packages) {
        log.info("Starting JDA-Commands...");
        dependencyInjector.index(clazz, packages);
        classFinders.forEach(classFinder -> interactionRegistry.index(classFinder.find()));
        updater.updateAllCommands();

        jdaContext.performTask(it -> it.addEventListener(JDAEventListener));
        log.info("Finished loading!");
        return this;
    }

    /// Creates a new JDACommands instance and starts the frameworks, including scanning the classpath for annotated classes.
    ///
    /// @param jda      the corresponding [JDA] instance
    /// @param clazz    a class of the classpath to scan
    /// @param packages package(s) to exclusively scan
    /// @return a new JDACommands instance
    public static JDACommands start(@NotNull JDA jda, @NotNull Class<?> clazz, @NotNull String... packages) {
        return builder(jda, clazz, packages).start();
    }

    /// Creates a new JDACommands instance and starts the frameworks, including scanning the classpath for annotated classes.
    ///
    /// @param shardManager the corresponding [ShardManager] instance
    /// @param clazz        a class of the classpath to scan
    /// @param packages     package(s) to exclusively scan
    /// @return a new JDACommands instance
    public static JDACommands start(@NotNull ShardManager shardManager, @NotNull Class<?> clazz, @NotNull String... packages) {
        return builder(shardManager, clazz, packages).start();
    }

    /// Create a new builder which uses a reflection based version of [ClassFinder].
    /// @param jda      the corresponding [JDA] instance
    /// @param clazz    a class of the classpath to scan
    /// @param packages package(s) to exclusively scan
    /// @return a new [JDACommandsBuilder]
    public static JDACommandsBuilder builder(JDA jda, Class<?> clazz, String... packages) {
        return new JDACommandsBuilder(new JDAContext(jda), clazz, packages);
    }

    /// Create a new builder which uses a reflection based version of [ClassFinder].
    /// @param shardManager      the corresponding [ShardManager] instance
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
