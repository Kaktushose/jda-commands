package com.github.kaktushose.jda.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.function.Consumer;

/**
 * Wrapper class for {@link JDA} and {@link ShardManager}. Use {@link #performTask(Consumer)} when you need to do
 * work with an {@link JDA} object.
 *
 * @author Kaktushose
 * @version 2.3.0
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
     * Performs an operation on either the {@link JDA} or the {@link ShardManager} object.
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
     * Gets the JDA instance. This can either be {@link JDA} or a {@link ShardManager}. Use {@link #isShardManager()}
     * to distinguish.
     *
     * @return the JDA instance.
     */
    public Object getJda() {
        return jda;
    }

    /**
     * Whether the JDA instance is a {@link ShardManager}.
     *
     * @return {@code true} if the JDA instance is a {@link ShardManager}
     */
    public boolean isShardManager() {
        return jda instanceof ShardManager;
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
