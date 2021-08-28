package com.github.kaktushose.jda.commands.entities;

import com.github.kaktushose.jda.commands.api.*;
import com.github.kaktushose.jda.commands.internal.CommandDispatcher;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to create a {@link JDACommands} instance. Although there may only be one active {@link JDACommands} instance,
 * a JDACommandsBuilder can be reused multiple times.
 *
 * @author Kaktushose
 * @version 1.1.1
 * @see JDACommands
 * @since 1.0.0
 */
public class JDACommandsBuilder {

    private final Object jda;
    private final boolean isShardManager;
    private final List<Object> providers;
    private String packageName;
    private CommandSettings defaultSettings;
    private Map<Long, CommandSettings> guildSettings;
    private EventParser eventParser;
    private CommandMapper commandMapper;
    private ArgumentParser argumentParser;
    private EmbedFactory embedFactory;
    private HelpMessageSender helpMessageSender;

    /**
     * Constructs a new JDACommandsBuilder using default values.
     *
     * @param jda the {@code JDA} needed to start the framework
     */
    public JDACommandsBuilder(@Nonnull JDA jda) {
        this(jda, false);
    }

    /**
     * Constructs a new JDACommandsBuilder using default values.
     *
     * @param shardManager the {@code ShardManager} needed to start the framework
     */
    public JDACommandsBuilder(@Nonnull ShardManager shardManager) {
        this(shardManager, true);
    }

    private JDACommandsBuilder(Object jda, boolean isShardManager) {
        this.defaultSettings = new CommandSettings("!", true, true, true, true);
        this.guildSettings = new HashMap<>();
        this.eventParser = new EventParser();
        this.commandMapper = new CommandMapper();
        this.argumentParser = new ArgumentParser();
        this.embedFactory = new EmbedFactory();
        this.helpMessageSender = new HelpMessageSender();
        this.jda = jda;
        this.isShardManager = isShardManager;
        providers = new ArrayList<>();
    }

    /**
     * Bootstraps a {@link JDACommands} instance using default values.
     *
     * @param jda the {@code JDA} needed to start the framework
     * @return a {@link JDACommands} instance that has started the initialization process
     */
    public static JDACommands startDefault(@Nonnull JDA jda) {
        return new JDACommandsBuilder(jda).build();
    }

    /**
     * Bootstraps a {@link JDACommands} instance using default values.
     *
     * @param shardManager the {@code ShardManager} needed to start the framework
     * @return a {@link JDACommands} instance that has started the initialization process
     */
    public static JDACommands startDefault(@Nonnull ShardManager shardManager) {
        return new JDACommandsBuilder(shardManager).build();
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
                .setDefaultSettings(new CommandSettings(prefix, true, true, true, true))
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
                .setDefaultSettings(new CommandSettings(prefix, true, true, true, true))
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
     * @param embedAtNotFound  whether to send an embed at a non-existing command or not
     * @return a {@link JDACommands} instance that has started the initialization process
     */
    public static JDACommands start(@Nonnull JDA jda, @Nullable String prefix, boolean ignoreBots, boolean ignoreLabelCase, boolean botMentionPrefix, boolean embedAtNotFound) {
        return new JDACommandsBuilder(jda)
                .setDefaultSettings(new CommandSettings(prefix, ignoreBots, ignoreLabelCase, botMentionPrefix, embedAtNotFound))
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
     * @param embedAtNotFound  whether to send an embed at a non-existing command or not
     * @return a {@link JDACommands} instance that has started the initialization process
     */
    public static JDACommands start(@Nonnull ShardManager shardManager, @Nullable String prefix, boolean ignoreBots, boolean ignoreLabelCase, boolean botMentionPrefix, boolean embedAtNotFound) {
        return new JDACommandsBuilder(shardManager)
                .setDefaultSettings(new CommandSettings(prefix, ignoreBots, ignoreLabelCase, botMentionPrefix, embedAtNotFound))
                .build();
    }

    /**
     * This method can be used to limit the command scanning only to a specific package.
     *
     * @param packageName the name of the package in which to search for command classes
     * @return the current instance to use fluent interface
     */
    public JDACommandsBuilder setCommandPackage(@Nullable String packageName) {
        this.packageName = packageName;
        return this;
    }

    /**
     * Sets the default {@link CommandSettings} that will be used to construct the {@link JDACommands}
     *
     * @param defaultSettings the {@link CommandSettings} to set
     * @return the current instance to use fluent interface
     * @see CommandSettings
     */
    public JDACommandsBuilder setDefaultSettings(@Nonnull CommandSettings defaultSettings) {
        this.defaultSettings = defaultSettings;
        return this;
    }

    /**
     * Sets the guild specific {@link CommandSettings} that will be used to construct the {@link JDACommands}
     *
     * @param guildSettings a map that contains the {@link CommandSettings} to for each guild. Key: the guild id Value: the prefix
     * @return the current instance to use fluent interface
     * @see CommandSettings
     */
    public JDACommandsBuilder setGuildCommandSettings(@Nonnull Map<Long, CommandSettings> guildSettings) {
        this.guildSettings = guildSettings;
        return this;
    }

    /**
     * Changes the {@link EventParser} used to parse incoming {@code GuildMessageReceivedEvent}s.
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
     * Registers a class containing {@link com.github.kaktushose.jda.commands.rewrite.annotations.Produces} methods that are
     * used for dependency injection.
     *
     * @param provider a class containing
     * @return the current instance to use fluent interface
     * @see com.github.kaktushose.jda.commands.rewrite.annotations.Produces
     */
    public JDACommandsBuilder addProvider(@Nonnull Object provider) {
        providers.add(provider);
        return this;
    }

    /**
     * Creates a new {@link JDACommands} using if present the provided values or else default values.
     *
     * @return a {@link JDACommands} instance that has started the initialization process
     * @see CommandSettings
     */
    public JDACommands build() {
        return new JDACommands(new CommandDispatcher(jda,
                isShardManager,
                defaultSettings,
                guildSettings,
                eventParser,
                commandMapper,
                argumentParser,
                embedFactory,
                helpMessageSender,
                providers), packageName);
    }

}
