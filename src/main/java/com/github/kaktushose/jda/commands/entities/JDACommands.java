package com.github.kaktushose.jda.commands.entities;

import com.github.kaktushose.jda.commands.internal.CommandDispatcher;
import com.github.kaktushose.jda.commands.internal.JedisInstanceHolder;
import com.github.kaktushose.jda.commands.internal.RedisSettingsHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Nonnull;

/**
 * This class is used to represent an active instance of this framework and provides access to the {@link CommandSettings}
 * and the {@link CommandList}.
 *
 * <p>It is basically an abstraction of {@link CommandDispatcher} in order to filter the access to methods and attributes.
 * An instance of this class may only be created through the {@link JDACommandsBuilder} due to the high
 * complexity of {@link CommandDispatcher}.
 *
 * @author Kaktushose
 * @version 1.0.0
 * @see JDACommandsBuilder
 * @since 1.0.0
 */
public class JDACommands {

    private final CommandDispatcher commandDispatcher;
    private final RedisSettingsHolder redisSettings;
    private static JDACommands INSTANCE;
    private final static Logger log = LoggerFactory.getLogger(JDACommands.class);

    /**
     * Constructs a JDACommands. The {@link JDACommandsBuilder} may be used instead of this constructor.
     *
     * @param commandDispatcher the {@link CommandDispatcher} instance to start the framework with
     * @see JDACommandsBuilder
     */
    public JDACommands(@Nonnull CommandDispatcher commandDispatcher, @Nonnull RedisSettingsHolder redisSettings) {
        this.commandDispatcher = commandDispatcher;
        this.redisSettings = redisSettings;
        INSTANCE = this;
        commandDispatcher.start(this);
    }

    /**
     * Get the {@link CommandSettings} of the current framework instance.
     *
     * @return the {@link CommandSettings} of this instance
     */
    public CommandSettings getSettings() {
        return commandDispatcher.getSettings();
    }

    /**
     * Get the {@link CommandList} of the current framework instance.
     *
     * @return the {@link CommandList} of this instance
     */
    public CommandList getCommands() {
        return commandDispatcher.getCommands();
    }

    /**
     * Get the redis settings for use in the {@link JedisInstanceHolder} and guild prefix saving
     *
     * @return the {@link RedisSettingsHolder} of this instance
     */
    public RedisSettingsHolder getRedisSettings() {
        return redisSettings;
    }

    /**
     * Get the current instance
     *
     * @return this {@link JDACommands} instance
     */
    public static JDACommands getInstance() {
        if (INSTANCE == null) {
            log.error("Fatal error occured! The JDACommand instance cannot be null!");
            return null;
        } else {
            return INSTANCE;
        }
    }

    /**
     * Shuts down the current framework instance. This will unregister the message listener and clear the {@link CommandList}.
     * Thus it is possible to build a new JDACommands instance after this method got invoked.
     */
    public void shutdown() {
        commandDispatcher.shutdown();
    }

}
