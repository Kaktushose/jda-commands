package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.annotations.interactions.CommandScope;
import com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.StringSelectMenu;
import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.SelectMenuDefinition;
import com.github.kaktushose.jda.commands.dispatching.DispatchingContext;
import com.github.kaktushose.jda.commands.dispatching.JDAEventListener;
import com.github.kaktushose.jda.commands.dispatching.adapter.internal.TypeAdapters;
import com.github.kaktushose.jda.commands.dispatching.expiration.ExpirationStrategy;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionControllerInstantiator;
import com.github.kaktushose.jda.commands.dispatching.middleware.internal.Middlewares;
import com.github.kaktushose.jda.commands.embeds.Embed;
import com.github.kaktushose.jda.commands.embeds.EmbedConfig;
import com.github.kaktushose.jda.commands.embeds.EmbedDataSource;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.embeds.internal.Embeds;
import com.github.kaktushose.jda.commands.i18n.I18n;
import com.github.kaktushose.jda.commands.internal.register.SlashCommandUpdater;
import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

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
    private final Embeds embeds;
    private final CommandDefinition.CommandConfig globalCommandConfig;
    private final I18n i18n;
    private final boolean shutdownJDA;

    JDACommands(JDAContext jdaContext,
                ExpirationStrategy expirationStrategy,
                TypeAdapters typeAdapters,
                Middlewares middlewares,
                ErrorMessageFactory errorMessageFactory,
                GuildScopeProvider guildScopeProvider,
                InteractionRegistry interactionRegistry,
                InteractionControllerInstantiator instanceProvider,
                InteractionDefinition.ReplyConfig globalReplyConfig,
                CommandDefinition.CommandConfig globalCommandConfig,
                I18n i18n,
                Embeds embeds,
                boolean shutdownJDA) {
        this.i18n = i18n;
        this.jdaContext = jdaContext;
        this.interactionRegistry = interactionRegistry;
        this.updater = new SlashCommandUpdater(jdaContext, guildScopeProvider, interactionRegistry, i18n.localizationFunction());
        this.jdaEventListener = new JDAEventListener(new DispatchingContext(middlewares, errorMessageFactory, interactionRegistry, typeAdapters, expirationStrategy, instanceProvider, globalReplyConfig, embeds, i18n));
        this.globalCommandConfig = globalCommandConfig;
        this.embeds = embeds;
        this.shutdownJDA = shutdownJDA;
    }

    /// Creates a new JDACommands instance and starts the frameworks, including scanning the classpath for annotated classes.
    /// This uses reflections for some functionality.
    ///
    /// If any exception while configuration/start of JDA-Commands is thrown, the JDA instance if shutdown per default.
    /// This can be configured by settings [JDACBuilder#shutdownJDA(boolean)].
    ///
    /// @param jda      the corresponding [JDA] instance
    /// @param clazz    a class of the classpath to scan
    /// @param packages package(s) to exclusively scan
    /// @return a new JDACommands instance
    public static JDACommands start(JDA jda, Class<?> clazz, String... packages) {
        return builder(jda, clazz, packages).start();
    }

    /// Creates a new JDACommands instance and starts the frameworks, including scanning the classpath for annotated classes.
    /// This uses reflections for some functionality.
    ///
    /// If any exception while configuration/start of JDA-Commands is thrown, the JDA instance if shutdown per default.
    /// This can be configured by settings [JDACBuilder#shutdownJDA(boolean)].
    ///
    /// @param shardManager the corresponding [ShardManager] instance
    /// @param clazz        a class of the classpath to scan
    /// @param packages     package(s) to exclusively scan
    /// @return a new JDACommands instance
    public static JDACommands start(ShardManager shardManager, Class<?> clazz, String... packages) {
        return builder(shardManager, clazz, packages).start();
    }

    /// Create a new builder.
    /// @param jda      the corresponding [JDA] instance
    /// @param clazz    a class of the classpath to scan
    /// @param packages package(s) to exclusively scan
    /// @return a new [JDACBuilder]
    public static JDACBuilder builder(JDA jda, Class<?> clazz, String... packages) {
        return new JDACBuilder(new JDAContext(jda), clazz, packages);
    }

    /// Create a new builder.
    ///
    /// @param shardManager the corresponding [ShardManager] instance
    /// @return a new [JDACBuilder]
    public static JDACBuilder builder(ShardManager shardManager, Class<?> clazz, String... packages) {
        return new JDACBuilder(new JDAContext(shardManager), clazz, packages);
    }

    void start(ClassFinder classFinder) {
        try {
            log.info("Starting JDA-Commands...");
            interactionRegistry.index(classFinder.search(Interaction.class), globalCommandConfig);
            updater.updateAllCommands();

            jdaContext.performTask(it -> it.addEventListener(jdaEventListener));
            log.info("Finished loading!");
        } catch (Exception e) {
            shutdown();
            throw JDACException.wrap(e);
        }

    }

    /// Shuts down this JDACommands instance, making it unable to receive any events from Discord.
    /// This will <b>not</b> unregister any slash commands.
    ///
    /// If [JDACBuilder#shutdownJDA()] is set to 'true', the underlying [JDA] or [ShardManager] instance will
    /// be shutdown too.
    public void shutdown() {
        jdaContext.performTask(jda -> jda.removeEventListener(jdaEventListener));

        if (shutdownJDA) {
            jdaContext.shutdown();
        }
    }

    /// Updates all slash commands that are registered with [CommandScope#GUILD]
    public void updateGuildCommands() {
        updater.updateGuildCommands();
    }

    /// Exposes the localization functionality of JDA-Commands to be used elsewhere in the application
    ///
    /// @return the [I18n] instance
    public I18n i18n() {
        return i18n;
    }

    /// Gets a [`Button`][com.github.kaktushose.jda.commands.annotations.interactions.Button] based on the method name
    /// and the given class and transforms it into a JDA [Button].
    ///
    /// The button will be [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) independent.
    /// This may be useful if you want to send a message without using the framework.
    ///
    /// @param button the name of the button in the format `FullClassNameWithPackage.method``
    /// @return the JDA [Button]
    public Button getButton(Class<?> origin, String button) {
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
    /// @param menu   the name of the button in the format `FullClassNameWithPackage.method``
    /// @return the JDA [SelectMenu]
    public SelectMenu getSelectMenu(Class<?> origin, String menu) {
        var id = String.valueOf((origin.getName() + menu).hashCode());
        var definition = interactionRegistry.find(SelectMenuDefinition.class, false, it -> it.definitionId().equals(id));
        return (SelectMenu) definition.toJDAEntity(CustomId.independent(definition.definitionId()));
    }

    /// Gets an [Embed] based on the given name.
    ///
    /// Use [#findEmbed(String)] if you cannot ensure that the [Embed] exists.
    ///
    /// @param name the name of the [Embed]
    /// @return the [Embed]
    /// @throws IllegalArgumentException if no [Embed] with the given name exists in the configured [data sources][EmbedConfig#sources(EmbedDataSource)]
    public Embed embed(String name) {
        return embeds.get(name);
    }

    /// Gets an [Embed] based on the given name and wraps it in an [Optional].
    ///
    /// Use this instead of [#embed(String)] if you cannot ensure that the [Embed] exists.
    ///
    /// @param name the name of the [Embed]
    /// @return an [Optional] holding the [Embed] or an empty [Optional] if an [Embed] with the given name doesn't exist
    public Optional<Embed> findEmbed(String name) {
        if (!embeds.exists(name)) {
            return Optional.empty();
        }
        return Optional.of(embeds.get(name));
    }
}
