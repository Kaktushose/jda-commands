package io.github.kaktushose.jdac;

import io.github.kaktushose.jdac.annotations.interactions.CommandScope;
import io.github.kaktushose.jdac.annotations.interactions.EntityMenu;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.annotations.interactions.StringMenu;
import io.github.kaktushose.jdac.definitions.description.ClassFinder;
import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.component.ButtonDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.SelectMenuDefinition;
import io.github.kaktushose.jdac.dispatching.JDAEventListener;
import io.github.kaktushose.jdac.embeds.Embed;
import io.github.kaktushose.jdac.embeds.EmbedConfig;
import io.github.kaktushose.jdac.embeds.EmbedDataSource;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import io.github.kaktushose.jdac.internal.JDAContext;
import io.github.kaktushose.jdac.internal.logging.JDACLogger;
import io.github.kaktushose.jdac.internal.register.CommandUpdater;
import io.github.kaktushose.jdac.property.JDACIntrospection;
import io.github.kaktushose.jdac.property.JDACProperty;
import io.github.kaktushose.jdac.property.JDACScope;
import io.github.kaktushose.jdac.property.events.FrameworkShutdownEvent;
import io.github.kaktushose.jdac.property.events.FrameworkStartEvent;
import io.github.kaktushose.jdac.property.internal.JDACInternalProperties;
import io.github.kaktushose.jdac.property.internal.JDACIntrospectionImpl;
import io.github.kaktushose.jdac.property.internal.extension.Extensions;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;

import java.util.*;

import static io.github.kaktushose.jdac.property.JDACProperty.LOCALIZATION_FUNCTION;
import static io.github.kaktushose.jdac.property.JDACProperty.LOCALIZE_COMMANDS;

/// The main entry point of the JDA-Commands framework. This class includes methods to manage the overall framework
/// while running.
///
/// Instances of this class can be created by using one of the "start" or "builder" methods.
public final class JDACommands {
    private static final Logger log = JDACLogger.getLogger(JDACommands.class);

    private final JDAEventListener jdaEventListener;
    private final CommandUpdater updater;
    private final JDACIntrospectionImpl introspection;

    JDACommands(JDACIntrospectionImpl.Builder builder) {
        this.introspection = builder
                .addFallback(JDACProperty.JDA_COMMANDS, _ -> this)
                .build();

        this.updater = new CommandUpdater(
                introspection.get(JDACInternalProperties.JDA_CONTEXT),
                introspection.get(JDACProperty.GUILD_SCOPE_PROVIDER),
                introspection.get(JDACInternalProperties.INTERACTION_REGISTRY),
                introspection.get(LOCALIZE_COMMANDS) ? introspection.get(LOCALIZATION_FUNCTION) : (_) -> Map.of()
        );

        this.jdaEventListener = new JDAEventListener(introspection);
    }

    /// Creates a new JDACommands instance and starts the frameworks, including scanning the classpath for annotated classes.
    /// This uses reflections for some functionality.
    ///
    /// If any exception while configuration/start of JDA-Commands is thrown, the JDA instance if shutdown per default.
    /// This can be configured by setting [JDACBuilder#shutdownJDA(boolean)] to `false`.
    ///
    /// @param jda the corresponding [JDA] instance
    /// @return a new JDACommands instance
    public static JDACommands start(JDA jda) {
        return builder(jda).start();
    }

    /// Creates a new JDACommands instance and starts the frameworks, including scanning the classpath for annotated classes.
    /// This uses reflections for some functionality.
    ///
    /// If any exception while configuration/start of JDA-Commands is thrown, the JDA instance if shutdown per default.
    /// This can be configured by setting [JDACBuilder#shutdownJDA(boolean)] to `false`.
    ///
    /// @param shardManager the corresponding [ShardManager] instance
    /// @return a new JDACommands instance
    public static JDACommands start(ShardManager shardManager) {
        return builder(shardManager).start();
    }

    /// Create a new builder.
    ///
    /// @param jda the corresponding [JDA] instance
    /// @return a new [JDACBuilder]
    public static JDACBuilder builder(JDA jda) {
        return new JDACBuilder(new JDAContext(jda));
    }

    /// Create a new builder.
    ///
    /// @param shardManager the corresponding [ShardManager] instance
    /// @return a new [JDACBuilder]
    public static JDACBuilder builder(ShardManager shardManager) {
        return new JDACBuilder(new JDAContext(shardManager));
    }

    void start(Extensions extensions) {
        ScopedValue.where(JDACIntrospectionImpl.INTROSPECTION, introspection).run(() -> {
            ClassFinder classFinder = introspection.get(JDACProperty.MERGED_CLASS_FINDER);

            introspection.get(JDACInternalProperties.INTERACTION_REGISTRY).index(classFinder.search(Interaction.class), introspection.get(JDACProperty.GLOBAL_COMMAND_CONFIG));
            updater.updateAllCommands();

            introspection.get(JDACInternalProperties.JDA_CONTEXT).performTask(it -> it.addEventListener(jdaEventListener), false);

            log.debug("Run Extension#onStart()");
            extensions.callOnStart(this);

            System.out.printf("\n%s\n\n", JDACException.errorMessage("starting-message"));

            introspection.publish(new FrameworkStartEvent());
        });
    }

    /// Shuts down this JDACommands instance, making it unable to receive any events from Discord.
    /// This will **not** unregister any slash commands.
    ///
    /// If [JDACBuilder#shutdownJDA(boolean)] is set to `true``, the underlying [JDA] or [ShardManager] instance will
    /// be shutdown too.
    public void shutdown() {
        introspection.publish(new FrameworkShutdownEvent());

        JDAContext jdaContext = introspection.get(JDACInternalProperties.JDA_CONTEXT);

        jdaContext.performTask(jda -> jda.removeEventListener(jdaEventListener), false);

        if (introspection.get(JDACProperty.SHUTDOWN_JDA)) {
            jdaContext.shutdown();
        }
    }

    /// Updates all slash commands that are registered with [CommandScope#GUILD]
    public void updateGuildCommands() {
        updateGuildCommands(List.of());
    }

    /// Updates all slash commands that are registered with [CommandScope#GUILD] for the given [Guild]s.
    ///
    /// @param guilds a [Collection] of guilds to update.
    public void updateGuildCommands(Collection<Guild> guilds) {
        updater.updateGuildCommands(Objects.requireNonNull(guilds));
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
        var definition = introspection.get(JDACInternalProperties.INTERACTION_REGISTRY).find(ButtonDefinition.class, false, it -> it.definitionId().equals(id));
        return definition.toJDAEntity(CustomId.independent(definition.definitionId()));
    }

    /// Gets a [StringMenu] or [EntityMenu] based on the method name and the given class and transforms it
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
        var definition = introspection.get(JDACInternalProperties.INTERACTION_REGISTRY).find(SelectMenuDefinition.class, false, it -> it.definitionId().equals(id));
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
        return introspection.get(JDACInternalProperties.EMBEDS).get(name);
    }

    /// Gets an [Embed] based on the given name and wraps it in an [Optional].
    ///
    /// Use this instead of [#embed(String)] if you cannot ensure that the [Embed] exists.
    ///
    /// @param name the name of the [Embed]
    /// @return an [Optional] holding the [Embed] or an empty [Optional] if an [Embed] with the given name doesn't exist
    public Optional<Embed> findEmbed(String name) {
        Embeds embeds = introspection.get(JDACInternalProperties.EMBEDS);

        if (!embeds.exists(name)) {
            return Optional.empty();
        }
        return Optional.of(embeds.get(name));
    }

    /// Gets the [JDACIntrospection] instance with scope [JDACScope#INITIALIZED].
    ///
    /// @return the [JDACIntrospection] instance
    public JDACIntrospection introspection() {
        return introspection;
    }

    /// Gets the value of a property.
    ///
    /// This is a shortcut for `JDACommands#introscpection#get`
    ///
    /// @param property the [JDACProperty] description
    /// @return T
    /// @param <T> the type of property
    public <T> T property(JDACProperty<T> property) {
        return introspection.get(property);
    }
}
