package io.github.kaktushose.jdac.internal;

import io.github.kaktushose.jdac.exceptions.InternalException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.ApplicationEmoji;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

/// Wrapper class for [JDA] and [ShardManager]. Use [#performTask(Consumer, boolean)] when you need to do work with an [JDA] object.
public final class JDAContext {

    public static final Logger log = LoggerFactory.getLogger(JDAContext.class);
    private static final InternalException EXCEPTION = new InternalException("jda-context-cast");
    private final Object context;

    /// Constructs a new JDAContext.
    ///
    /// @param shardManager the [ShardManager] object
    public JDAContext(ShardManager shardManager) {
        this.context = shardManager;
    }

    /// Constructs a new JDAContext.
    ///
    /// @param jda the [JDA] object
    public JDAContext(JDA jda) {
        this.context = jda;
    }

    /// Performs an operation on either the [JDA] object or on all shards.
    ///
    /// @param consumer the operation to perform
    /// @param onlyFirstShard whether this task should only be executed on the first shard if a [ShardManager] is used
    public void performTask(Consumer<JDA> consumer, boolean onlyFirstShard) {
        switch (context) {
            case ShardManager shardManager -> {
                if (onlyFirstShard) {
                    consumer.accept(shardManager.getShardById(0));
                    return;
                }

                shardManager.getShardCache().forEach(consumer);
            }
            case JDA jda -> consumer.accept(jda);
            default -> throw EXCEPTION;
        }
    }

    /// [SnowflakeCacheView] of all cached [Guild]s.
    ///
    /// @return the [SnowflakeCacheView]
    public SnowflakeCacheView<Guild> getGuildCache() {
        return switch (context) {
            case ShardManager shardManager -> shardManager.getGuildCache();
            case JDA jda -> jda.getGuildCache();
            default -> throw EXCEPTION;
        };
    }

    public List<ApplicationEmoji> applicationEmojis() {
        try {
            return switch (context) {
                case JDA jda -> jda.retrieveApplicationEmojis().complete();
                case ShardManager shardManager -> {
                    JDA first = shardManager.getShardById(0);
                    first.awaitReady();
                    yield first.retrieveApplicationEmojis().complete();
                }
                default -> throw EXCEPTION;
            };
        } catch (InterruptedException e) { // edge case, fine to just rethrow
            throw new RuntimeException(e);
        }
    }

    /// Shutdown the underlying [JDA] or [ShardManager] instance
    public void shutdown() {
        log.warn("JDA was shutdown by JDA-Commands, this might be due to an exception during the init/start process. To disable this behaviour set JDACBuilder#shutdownJDA(boolean) to false");
        switch (context) {
            case ShardManager manager -> manager.shutdown();
            case JDA jda -> jda.shutdown();
            default -> throw EXCEPTION;
        }
    }
}
