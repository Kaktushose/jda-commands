package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.annotations.interactions.CommandScope;
import com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu;
import com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu;
import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.SelectMenuDefinition;
import com.github.kaktushose.jda.commands.dispatching.DispatchingContext;
import com.github.kaktushose.jda.commands.dispatching.JDAEventListener;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import com.github.kaktushose.jda.commands.dispatching.expiration.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.instance.InstanceProvider;
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
    private final JDAEventListener jdaEventListener;
    private final InteractionRegistry interactionRegistry;
    private final SlashCommandUpdater updater;

    JDACommands(
            JDAContext jdaContext,
            ExpirationStrategy expirationStrategy,
            TypeAdapters typeAdapters,
            Middlewares middlewares,
            ErrorMessageFactory errorMessageFactory,
            GuildScopeProvider guildScopeProvider,
            InteractionRegistry interactionRegistry,
            InstanceProvider instanceProvider,
            InteractionDefinition.ReplyConfig globalReplyConfig) {
        this.jdaContext = jdaContext;
        this.interactionRegistry = interactionRegistry;
        this.updater = new SlashCommandUpdater(jdaContext, guildScopeProvider, interactionRegistry);
        this.jdaEventListener = new JDAEventListener(new DispatchingContext(middlewares, errorMessageFactory, interactionRegistry, typeAdapters, expirationStrategy, instanceProvider, globalReplyConfig));
    }

    JDACommands start(Collection<ClassFinder> classFinders, Class<?> clazz, String[] packages) {
        log.info("Starting JDA-Commands...");
        classFinders.forEach(classFinder -> interactionRegistry.index(classFinder.find()));
        updater.updateAllCommands();

        jdaContext.performTask(it -> it.addEventListener(jdaEventListener));
        log.info("Finished loading!");
        return this;
    }

    /// Creates a new JDACommands instance and starts the frameworks, including scanning the classpath for annotated classes.
    /// This uses reflections for some functionality.
    ///
    /// @param jda      the corresponding [JDA] instance
    /// @param clazz    a class of the classpath to scan
    /// @param packages package(s) to exclusively scan
    /// @return a new JDACommands instance
    @NotNull
    public static JDACommands start(@NotNull JDA jda, @NotNull Class<?> clazz, @NotNull String... packages) {
        return builder(jda, clazz, packages).start();
    }

    /// Creates a new JDACommands instance and starts the frameworks, including scanning the classpath for annotated classes.
    /// This uses reflections for some functionality.
    ///
    /// @param shardManager the corresponding [ShardManager] instance
    /// @param clazz        a class of the classpath to scan
    /// @param packages     package(s) to exclusively scan
    /// @return a new JDACommands instance
    @NotNull
    public static JDACommands start(@NotNull ShardManager shardManager, @NotNull Class<?> clazz, @NotNull String... packages) {
        return builder(shardManager, clazz, packages).start();
    }

    /// Create a new builder.
    /// @param jda      the corresponding [JDA] instance
    /// @param clazz    a class of the classpath to scan
    /// @param packages package(s) to exclusively scan
    /// @return a new [JDACommandsBuilder]
    @NotNull
    public static JDACommandsBuilder builder(@NotNull JDA jda, @NotNull Class<?> clazz, @NotNull String... packages) {
        return new JDACommandsBuilder(new JDAContext(jda), clazz, packages);
    }

    /// Create a new builder.
    /// @param shardManager      the corresponding [ShardManager] instance
    /// @return a new [JDACommandsBuilder]
    @NotNull
    public static JDACommandsBuilder builder(@NotNull ShardManager shardManager, @NotNull Class<?> clazz, @NotNull String... packages) {
        return new JDACommandsBuilder(new JDAContext(shardManager), clazz, packages);
    }

    /**
     * Shuts down this JDACommands instance, making it unable to receive any events from Discord.
     * This will <b>not</b> unregister any slash commands.
     */
    public void shutdown() {
        jdaContext.performTask(jda -> jda.removeEventListener(jdaEventListener));
    }

    /// Updates all slash commands that are registered with [CommandScope#GUILD]
    public void updateGuildCommands() {
        updater.updateGuildCommands();
    }

    /// Gets a [`Button`][com.github.kaktushose.jda.commands.annotations.interactions.Button] based on the method name
    /// and the given class and transforms it into a JDA [Button].
    ///
    /// The button will be [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) independent.
    /// This may be useful if you want to send a message without using the framework.
    ///
    /// @param button the name of the button in the format `FullClassNameWithPackage.method``
    /// @return the JDA [Button]
    @NotNull
    public Button getButton(@NotNull Class<?> origin, @NotNull String button) {
        var id = String.valueOf((origin.getName() + button).hashCode());
        var definition = interactionRegistry.find(ButtonDefinition.class, false, it -> it.definitionId().equals(id));
        return definition.toJDAEntity(CustomId.independent(definition.definitionId()));
    }

    /// Gets a [StringSelectMenu] or [EntitySelectMenu] based on the method name and the given class and transforms it
    /// into a JDA [SelectMenu].
    ///
    /// The select menu will be [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) independent.
    /// This may be useful if you want to send a component without using the framework.
    ///
    /// @param origin the [Class] of the method
    /// @param menu the name of the button in the format `FullClassNameWithPackage.method``
    /// @return the JDA [SelectMenu]
    @NotNull
    public SelectMenu getSelectMenu(@NotNull Class<?> origin, @NotNull String menu) {
        var id = String.valueOf((origin.getName() + menu).hashCode());
        var definition = interactionRegistry.find(SelectMenuDefinition.class, false, it -> it.definitionId().equals(id));
        return (SelectMenu) definition.toJDAEntity(CustomId.independent(definition.definitionId()));
    }
}
