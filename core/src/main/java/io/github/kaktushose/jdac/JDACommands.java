package io.github.kaktushose.jdac;

import io.github.kaktushose.jdac.annotations.interactions.CommandScope;
import io.github.kaktushose.jdac.annotations.interactions.EntitySelectMenu;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.annotations.interactions.StringSelectMenu;
import io.github.kaktushose.jdac.definitions.description.ClassFinder;
import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.component.ButtonDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.SelectMenuDefinition;
import io.github.kaktushose.jdac.dispatching.FrameworkContext;
import io.github.kaktushose.jdac.dispatching.JDAEventListener;
import io.github.kaktushose.jdac.embeds.Embed;
import io.github.kaktushose.jdac.embeds.EmbedConfig;
import io.github.kaktushose.jdac.embeds.EmbedDataSource;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.internal.register.SlashCommandUpdater;
import io.github.kaktushose.jdac.message.MessageResolver;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.scope.GuildScopeProvider;
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
    private final FrameworkContext frameworkContext;
    private final SlashCommandUpdater updater;
    private final boolean shutdownJDA;

    JDACommands(FrameworkContext frameworkContext,
                JDAContext jdaContext,
                GuildScopeProvider guildScopeProvider,
                boolean shutdownJDA) {
        this.frameworkContext = frameworkContext;
        this.jdaContext = jdaContext;
        this.updater = new SlashCommandUpdater(jdaContext, guildScopeProvider, frameworkContext.interactionRegistry());
        this.jdaEventListener = new JDAEventListener(frameworkContext);
        this.shutdownJDA = shutdownJDA;
    }

    /// Creates a new JDACommands instance and starts the frameworks, including scanning the classpath for annotated classes.
    /// This uses reflections for some functionality.
    ///
    /// If any exception while configuration/start of JDA-Commands is thrown, the JDA instance if shutdown per default.
    /// This can be configured by setting [JDACBuilder#shutdownJDA(boolean)] to `false`.
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
    /// This can be configured by setting [JDACBuilder#shutdownJDA(boolean)] to `false`.
    ///
    /// @param shardManager the corresponding [ShardManager] instance
    /// @param clazz        a class of the classpath to scan
    /// @param packages     package(s) to exclusively scan
    /// @return a new JDACommands instance
    public static JDACommands start(ShardManager shardManager, Class<?> clazz, String... packages) {
        return builder(shardManager, clazz, packages).start();
    }

    /// Create a new builder.
    ///
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
        frameworkContext.interactionRegistry().index(classFinder.search(Interaction.class), frameworkContext.globalCommandConfig());
        updater.updateAllCommands();

        jdaContext.performTask(it -> it.addEventListener(jdaEventListener), false);
        log.info("Finished loading!");

    }

    /// Shuts down this JDACommands instance, making it unable to receive any events from Discord.
    /// This will **not** unregister any slash commands.
    ///
    /// If [JDACBuilder#shutdownJDA()] is set to `true``, the underlying [JDA] or [ShardManager] instance will
    /// be shutdown too.
    public void shutdown() {
        jdaContext.performTask(jda -> jda.removeEventListener(jdaEventListener), false);

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
        return frameworkContext.i18n();
    }

    /// Exposes the message resolver functionality of JDA-Commands to be used elsewhere in the application.
    /// This can be used to do localization and/or resolve emoji aliases used in messages.
    ///
    /// @return the [MessageResolver] instance
    public MessageResolver messageResolver() {
        return frameworkContext.messageResolver();
    }

    /// Gets a [`Button`][io.github.kaktushose.jdac.annotations.interactions.Button] based on the method name
    /// and the given class and transforms it into a JDA [Button].
    ///
    /// The button will be [`Runtime`]({@docRoot}/index.html#runtime-concept-heading) independent.
    /// This may be useful if you want to send a message without using the framework.
    ///
    /// @param button the name of the button in the format `FullClassNameWithPackage.method``
    /// @return the JDA [Button]
    public Button getButton(Class<?> origin, String button) {
        var id = String.valueOf((origin.getName() + button).hashCode());
        var definition = frameworkContext.interactionRegistry().find(ButtonDefinition.class, false, it -> it.definitionId().equals(id));
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
        var definition = frameworkContext.interactionRegistry().find(SelectMenuDefinition.class, false, it -> it.definitionId().equals(id));
        return (SelectMenu) definition.toJDAEntity(CustomId.independent(definition.definitionId()));
    }

    /// Gets an [Embed] based on the given name.
    ///
    /// Use [#findEmbed(String)] if you cannot ensure that the [Embed] exists.
    ///
    /// @param name the name of the [Embed]
    /// @return the [Embed]
    /// @throws IllegalArgumentException if no [Embed] with the given name exists in the configured [data sources][EmbedConfig#sources(EmbedDataSource...)]
    public Embed embed(String name) {
        return frameworkContext.embeds().get(name);
    }

    /// Gets an [Embed] based on the given name and wraps it in an [Optional].
    ///
    /// Use this instead of [#embed(String)] if you cannot ensure that the [Embed] exists.
    ///
    /// @param name the name of the [Embed]
    /// @return an [Optional] holding the [Embed] or an empty [Optional] if an [Embed] with the given name doesn't exist
    public Optional<Embed> findEmbed(String name) {
        Embeds embeds = frameworkContext.embeds();

        if (!embeds.exists(name)) {
            return Optional.empty();
        }
        return Optional.of(embeds.get(name));
    }
}
