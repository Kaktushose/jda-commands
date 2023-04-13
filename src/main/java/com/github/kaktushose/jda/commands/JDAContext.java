package com.github.kaktushose.jda.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;

import java.util.List;
import java.util.function.Consumer;

/**
 * Wrapper class for {@link JDA} and {@link ShardManager}. Use {@link #performTask(Consumer)} when you need to do
 * work with an {@link JDA} object.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 2.3.0
 */
public class JDAContext {

    private final Object jda;

    /**
     * Constructs a new JDAContext.
     *
     * @param jda the {@link JDA} or {@link ShardManager} object
     */
    public JDAContext(Object jda) {
        this.jda = jda;
    }

    /**
     * Performs an operation on either the {@link JDA} object or on all shards.
     *
     * @param consumer the operation to perform
     */
    public void performTask(Consumer<JDA> consumer) {
        if (jda instanceof ShardManager) {
            ((ShardManager) jda).getShardCache().forEach(consumer);
        } else if (jda instanceof JDA) {
            consumer.accept((JDA) jda);
        } else {
            throw new IllegalArgumentException(String.format("Cannot cast %s", jda.getClass().getSimpleName()));
        }
    }

    /**
     * Gets the JDA instance as an Object. This can either be {@link JDA} or a {@link ShardManager}.
     * Use {@link #isShardManager()} to distinguish.
     *
     * @return the JDA instance.
     */
    public Object getJDAObject() {
        return jda;
    }

    /**
     * Whether the JDA instance is a {@link ShardManager}.
     *
     * @return {@code true} if the JDA instance is a {@link ShardManager}
     * @deprecated
     */
    public boolean isShardManager() {
        return jda instanceof ShardManager;
    }

    /**
     * An unmodifiable List of all {@link Guild Guilds} that the logged account is connected to.
     * If this account is not connected to any {@link Guild Guilds}, this will return an empty list.
     * This copies the backing store into a list. This means every call creates a new list with O(n) complexity.
     * It is recommended to store this into a local variable or use getGuildCache() and use its more efficient
     * versions of handling these values.
     *
     * @return Possibly-empty list of all the {@link Guild Guilds} that this account is connected to.
     */
    public List<Guild> getGuilds() {
        if (jda instanceof ShardManager) {
            return ((ShardManager) jda).getGuilds();
        } else if (jda instanceof JDA) {
            return ((JDA) jda).getGuilds();
        } else {
            throw new IllegalArgumentException(String.format("Cannot cast %s", jda.getClass().getSimpleName()));
        }
    }

    /**
     * {@link SnowflakeCacheView} of all cached {@link Guild Guilds}.
     *
     * @return {@link SnowflakeCacheView}
     */
    public SnowflakeCacheView<Guild> getGuildCache() {
        if (jda instanceof ShardManager) {
            return ((ShardManager) jda).getGuildCache();
        } else if (jda instanceof JDA) {
            return ((JDA) jda).getGuildCache();
        } else {
            throw new IllegalArgumentException(String.format("Cannot cast %s", jda.getClass().getSimpleName()));
        }
    }

    /**
     * Gets the JDA instance as {@link JDA}.
     *
     * @return the JDA instance as {@link JDA}
     */
    public JDA getAsJDA() {
        return (JDA) jda;
    }

    /**
     * Gets the JDA instance as {@link ShardManager}.
     *
     * @return the JDA instance as {@link ShardManager}
     */
    public ShardManager getAsShardManager() {
        return (ShardManager) jda;
    }
}
