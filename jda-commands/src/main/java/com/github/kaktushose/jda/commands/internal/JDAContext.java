package com.github.kaktushose.jda.commands.internal;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

/// Wrapper class for [JDA] and [ShardManager]. Use [#performTask(Consumer)] when you need to do work with an [JDA] object.
@ApiStatus.Internal
public final class JDAContext {

    private final Object context;

    /// Constructs a new JDAContext.
    ///
    /// @param context the [JDA] or [ShardManager] object
    public JDAContext(Object context) {
        this.context = context;
    }

    /// Performs an operation on either the [JDA] object or on all shards.
    ///
    /// @param consumer the operation to perform
    public void performTask(Consumer<JDA> consumer) {
        switch (context) {
            case ShardManager shardManager -> shardManager.getShardCache().forEach(consumer);
            case JDA jda -> consumer.accept(jda);
            default ->
                    throw new IllegalArgumentException(String.format("Cannot cast %s", context.getClass().getSimpleName()));
        }
    }

    /// [SnowflakeCacheView] of all cached [Guild]s.
    ///
    /// @return [SnowflakeCacheView]
    public SnowflakeCacheView<Guild> getGuildCache() {
        return switch (context) {
            case ShardManager shardManager -> shardManager.getGuildCache();
            case JDA jda -> jda.getGuildCache();
            default ->
                    throw new IllegalArgumentException(String.format("Cannot cast %s", context.getClass().getSimpleName()));
        };
    }
}
