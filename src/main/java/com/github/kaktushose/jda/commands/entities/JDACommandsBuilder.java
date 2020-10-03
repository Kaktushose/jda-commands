package com.github.kaktushose.jda.commands.entities;

import com.github.kaktushose.jda.commands.api.*;
import com.github.kaktushose.jda.commands.internal.CommandDispatcher;
import com.github.kaktushose.jda.commands.internal.RedisSettingsHolder;
import com.github.kaktushose.jda.commands.internal.YamlLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to create a {@link JDACommands} instance. Although there may only be one active {@link JDACommands} instance,
 * a JDACommandsBuilder can be reused multiple times.
 *
 * @author Kaktushose
 * @version 1.0.0
 * @see JDACommands
 * @since 1.0.0
 */
public class JDACommandsBuilder {

    private static final Logger log = LoggerFactory.getLogger(JDACommands.class);
    private final Object jda;
    private final boolean isShardManager;
    private final List<Provider> providers;
    private CommandSettings settings;
    private EventParser eventParser;
    private CommandMapper commandMapper;
    private ArgumentParser argumentParser;
    private EmbedFactory embedFactory;
    private HelpMessageSender helpMessageSender;
    private boolean isRedisEnabled;
    private String redisHost;
    private Integer redisPort;
    private Integer redisDatabase;

    /**
     * Constructs a new JDACommandsBuilder using default values.
     *
     * @param jda the {@code JDA} needed to start the framework
     */
    public JDACommandsBuilder(@Nonnull JDA jda) {
        this.settings = new CommandSettings("!", true, true, true, false, "127.0.0.1", 6379, 0);
        this.eventParser = new EventParser();
        this.commandMapper = new CommandMapper();
        this.argumentParser = new ArgumentParser();
        this.embedFactory = new EmbedFactory();
        this.helpMessageSender = new HelpMessageSender();
        providers = new ArrayList<>();
        this.jda = jda;
        isShardManager = false;
        isRedisEnabled = false;
        redisHost = null;
        redisPort = null;
        redisDatabase = 0;
    }

    /**
     * Constructs a new JDACommandsBuilder using default values.
     *
     * @param shardManager the {@code ShardManager} needed to start the framework
     */
    public JDACommandsBuilder(@Nonnull ShardManager shardManager) {
        this.settings = new CommandSettings("!", true, true, true, false, "127.0.0.1", 6379, 0);
        this.eventParser = new EventParser();
        this.commandMapper = new CommandMapper();
        this.argumentParser = new ArgumentParser();
        this.helpMessageSender = new HelpMessageSender();
        providers = new ArrayList<>();
        jda = shardManager;
        isShardManager = true;
        isRedisEnabled = false;
        redisHost = null;
        redisPort = null;
        redisDatabase = 0;
    }

    /**
     * Bootstraps a {@link JDACommands} instance using default values.
     *
     * @param jda the {@code JDA} needed to start the framework
     * @return a {@link JDACommands} instance that has started the initialization process
     */
    @SuppressWarnings("unused")
    public static JDACommands startDefault(@Nonnull JDA jda) {
        return new JDACommandsBuilder(jda)
               // .setRedisOptions(false, "", 6379, 0)
                .build();
    }

    /**
     * Bootstraps a {@link JDACommands} instance using default values.
     *
     * @param shardManager the {@code ShardManager} needed to start the framework
     * @return a {@link JDACommands} instance that has started the initialization process
     */
    @SuppressWarnings("unused")
    public static JDACommands startDefault(@Nonnull ShardManager shardManager) {
        return new JDACommandsBuilder(shardManager)
               // .setRedisOptions(false, "", 6379, 0)
                .build();
    }

    /**
     * Bootstraps a {@link JDACommands} instance using the given prefix and default values.
     *
     * @param jda    the {@code JDA} needed to start the framework
     * @param prefix the prefix the framework will listen to
     * @return a {@link JDACommands} instance that has started the initialization process
     */
    public static JDACommands start(@Nonnull JDA jda, @Nullable String prefix) {
        return new JDACommandsBuilder(jda)
                .setSettings(new CommandSettings(prefix, true, true, true, false, "127.0.0.1", 6379, 0))
             //   .setRedisOptions(false, "", 6379, 0)
                .build();
    }

    /**
     * Bootstraps a {@link JDACommands} instance using the given prefix and default values.
     *
     * @param shardManager the {@code ShardManager} needed to start the framework
     * @param prefix       the prefix the framework will listen to
     * @return a {@link JDACommands} instance that has started the initialization process
     */
    public static JDACommands start(@Nonnull ShardManager shardManager, @Nullable String prefix) {
        return new JDACommandsBuilder(shardManager)
                .setSettings(new CommandSettings(prefix, true, true, true, false, "127.0.0.1", 6379, 0))
           //     .setRedisOptions(false, "", 6379, 0)
                .build();
    }

    /**
     * Bootstraps a {@link JDACommands} instance using the given values.
     *
     * @param jda              the {@code JDA} needed to start the framework
     * @param prefix           the prefix the framework will listen to
     * @param ignoreBots       whether the framework should ignore messages from Discord Bots or not
     * @param ignoreLabelCase  whether the command mapper should be case sensitive or not
     * @param botMentionPrefix whether to allow a bot mention to be a valid prefix or not
     * @param isRedisEnabled   whether redis guild prefix saving should be used or not
     * @param redisHost        the server redis should connect to in case of using it
     * @param redisPort        the port the redis server is running on
     * @param redisDatabase    the database redis is saving the prefixes in
     * @return a {@link JDACommands} instance that has started the initialization process
     */
    public static JDACommands start(@Nonnull JDA jda, @Nullable String prefix, boolean ignoreBots, boolean ignoreLabelCase, boolean botMentionPrefix, boolean isRedisEnabled, String redisHost, Integer redisPort, Integer redisDatabase) {
        return new JDACommandsBuilder(jda)
                .setSettings(new CommandSettings(prefix, ignoreBots, ignoreLabelCase, botMentionPrefix, isRedisEnabled, redisHost, redisPort, redisDatabase))
           //     .setRedisOptions(isRedisEnabled, redisHost, redisPort, redisDatabase)
                .build();
    }

    /**
     * Bootstraps a {@link JDACommands} instance using the given values.
     *
     * @param shardManager     the {@code ShardManager} needed to start the framework
     * @param prefix           the prefix the framework will listen to
     * @param ignoreBots       whether the framework should ignore messages from Discord Bots or not
     * @param ignoreLabelCase  whether the command mapper should be case sensitive or not
     * @param botMentionPrefix whether to allow a bot mention to be a valid prefix or not
     * @param isRedisEnabled   whether redis guild prefix saving should be used or not
     * @param redisHost        the server redis should connect to in case of using it
     * @param redisPort        the port the redis server is running on
     * @param redisDatabase    the database redis is saving the prefixes in
     * @return a {@link JDACommands} instance that has started the initialization process
     */
    public static JDACommands start(@Nonnull ShardManager shardManager, @Nullable String prefix, boolean ignoreBots, boolean ignoreLabelCase, boolean botMentionPrefix, boolean isRedisEnabled, String redisHost, Integer redisPort, Integer redisDatabase) {
        return new JDACommandsBuilder(shardManager)
                .setSettings(new CommandSettings(prefix, ignoreBots, ignoreLabelCase, botMentionPrefix, isRedisEnabled, redisHost, redisPort, redisDatabase))
             //   .setRedisOptions(isRedisEnabled, redisHost, redisPort, redisDatabase)
                .build();
    }

    /**
     * Sets the {@link CommandSettings} that will be used to construct the {@link JDACommands}
     *
     * @param settings the {@link CommandSettings} to set
     * @return the current instance to use fluent interface
     * @see CommandSettings
     */
    public JDACommandsBuilder setSettings(@Nonnull CommandSettings settings) {
        this.settings = settings;
        return this;
    }

    /**
     * Sets the redis options for setting up the caching
     *
     * @param isRedisEnabled set to true, if enabled, <strong>otherwise other options will be ignored</strong>!
     * @param redisHost the hostname/ipv4 to set
     * @param redisPort the port to set
     * @return the current instance to use fluent interface
     */

    public JDACommandsBuilder setRedisOptions(boolean isRedisEnabled, @Nonnull String redisHost, @Nonnull Integer redisPort, @Nonnull Integer redisDatabase){
        this.isRedisEnabled = isRedisEnabled;
        this.redisHost = redisHost;
        this.redisPort = redisPort;
        this.redisDatabase = redisDatabase;
        return this;
    }


    /**
     * Changes the {@link EventParser} used to parse incoming {@code MessageReceivedEvent}s.
     *
     * @param eventParser the new {@link EventParser to use}
     * @return the current instance to use fluent interface
     * @see EventParser
     */
    public JDACommandsBuilder setEventParser(@Nonnull EventParser eventParser) {
        this.eventParser = eventParser;
        return this;
    }

    /**
     * Changes the {@link CommandMapper} used to find a command mapping.
     *
     * @param commandMapper the new {@link CommandMapper} to use
     * @return the current instance to use fluent interface
     * @see CommandMapper
     */
    public JDACommandsBuilder setCommandMapper(@Nonnull CommandMapper commandMapper) {
        this.commandMapper = commandMapper;
        return this;
    }

    /**
     * Changes the {@link ArgumentParser} used to parse String inputs to objects.
     *
     * @param argumentParser the new {@link ArgumentParser} to use
     * @return the current instance to use fluent interface
     * @see ArgumentParser
     */
    public JDACommandsBuilder setArgumentParser(@Nonnull ArgumentParser argumentParser) {
        this.argumentParser = argumentParser;
        return this;
    }

    /**
     * Changes the factory used to create Embeds, e.g. for help messages.
     *
     * @param embedFactory the new {@link EmbedFactory} to use
     * @return the current instance to use fluent interface
     * @see EmbedFactory
     */
    public JDACommandsBuilder setEmbedFactory(@Nonnull EmbedFactory embedFactory) {
        this.embedFactory = embedFactory;
        return this;
    }

    /**
     * Changes the {@link HelpMessageSender} used to send default and specific help messages.
     *
     * @param helpMessageSender the new {@link HelpMessageSender} to use
     * @return the current instance to use fluent interface
     */
    public JDACommandsBuilder setHelpMessageSender(@Nonnull HelpMessageSender helpMessageSender) {
        this.helpMessageSender = helpMessageSender;
        return this;
    }

    /**
     * Adds a {@link Provider} used to get the dependencies that will be injected.
     *
     * @param provider the {@link Provider to add}
     * @return the current instance to use fluent interface
     * @see Provider
     */
    public JDACommandsBuilder addProvider(@Nonnull Provider provider) {
        providers.add(provider);
        return this;
    }

    /**
     * Creates a new {@link JDACommands} using if present the provided values or else default values.
     *
     * <p>Please note that this method will search for a <em>settings.yaml</em> file. If such a file is found, it
     * will be loaded and may override previous settings.
     *
     * @return a {@link JDACommands} instance that has started the initialization process
     * @see CommandSettings
     */
    @SuppressWarnings("ConstantConditions")
    public JDACommands build() {
        try {
            settings = YamlLoader.load(JDACommands.class.getClassLoader().getResource("settings.yaml"));
            log.debug("Found a settings file. Maybe overriding given runtime values");
        } catch (IOException | NullPointerException ignore) {
            log.debug("No settings file found");
        }
        return new JDACommands(new CommandDispatcher(jda,
                isShardManager,
                settings,
                eventParser,
                commandMapper,
                argumentParser,
                embedFactory,
                helpMessageSender,
                providers),
                new RedisSettingsHolder(this.isRedisEnabled, this.redisHost, this.redisPort, this.redisDatabase));
    }

}
