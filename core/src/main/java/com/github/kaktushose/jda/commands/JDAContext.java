package com.github.kaktushose.jda.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/// Wrapper class for [JDA] and [ShardManager]. Use [#performTask(Consumer)] when you need to do work with an [JDA] object.
public final class JDAContext {

    public static final Logger log = LoggerFactory.getLogger(JDAContext.class);

    private final Object context;

    /// Constructs a new JDAContext.
    ///
    /// @param shardManager the [ShardManager] object
    JDAContext(ShardManager shardManager) {
        this.context = shardManager;
    }

    /// Constructs a new JDAContext.
    ///
    /// @param jda the [JDA] object
    JDAContext(JDA jda) {
        this.context = jda;
    }

    /// Performs an operation on either the [JDA] object or on all shards.
    ///
    /// @param consumer the operation to perform
    public void performTask(Consumer<JDA> consumer) {
        switch (context) {
            case ShardManager shardManager -> shardManager.getShardCache().forEach(consumer);
            case JDA jda -> consumer.accept(jda);
            default ->
                    throw new JDACException.Internal("Cannot cast context to either JDA oder ShardManager class.");
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
                    throw new JDACException.Internal("Cannot cast context to either JDA oder ShardManager class.");
        };
    }

    /// Shutdown the underlying [JDA] or [ShardManager] instance
    public void shutdown() {
        log.warn("JDA was shutdown by JDA-Commands, this might be due to an exception during the init/start process. To disable this behaviour call JDACBuilder#shutdownJDA");
        switch (context) {
            case ShardManager manager -> manager.shutdown();
            case JDA jda -> jda.shutdown();
            default -> throw new JDACException.Internal("Cannot cast context to either JDA oder ShardManager class.");
        }
    }
}
