package com.github.kaktushose.jda.commands.internal;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Consumer;

/**
 * Wrapper class for {@link JDA} and {@link ShardManager}. Use {@link #performTask(Consumer)} when you need to do
 * work with an {@link JDA} object.
 *
 * @since 2.3.0
 */
@ApiStatus.Internal
public final class JDAContext {

    private final Object context;

    /**
     * Constructs a new JDAContext.
     *
     * @param context the {@link JDA} or {@link ShardManager} object
     */
    public JDAContext(Object context) {
        this.context = context;
    }

    /**
     * Performs an operation on either the {@link JDA} object or on all shards.
     *
     * @param consumer the operation to perform
     */
    public void performTask(Consumer<JDA> consumer) {
        switch (context) {
            case ShardManager shardManager -> shardManager.getShardCache().forEach(consumer);
            case JDA jda -> consumer.accept(jda);
            default ->
                    throw new IllegalArgumentException(String.format("Cannot cast %s", context.getClass().getSimpleName()));
        }
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
        return switch (context) {
            case ShardManager shardManager -> shardManager.getGuilds();
            case JDA jda -> jda.getGuilds();
            default ->
                    throw new IllegalArgumentException(String.format("Cannot cast %s", context.getClass().getSimpleName()));
        };
    }

    /**
     * {@link SnowflakeCacheView} of all cached {@link Guild Guilds}.
     *
     * @return {@link SnowflakeCacheView}
     */
    public SnowflakeCacheView<Guild> getGuildCache() {
        return switch (context) {
            case ShardManager shardManager -> shardManager.getGuildCache();
            case JDA jda -> jda.getGuildCache();
            default ->
                    throw new IllegalArgumentException(String.format("Cannot cast %s", context.getClass().getSimpleName()));
        };
    }
}
