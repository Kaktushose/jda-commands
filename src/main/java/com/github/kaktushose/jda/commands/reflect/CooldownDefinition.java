package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.interactions.Cooldown;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Representation of a command cooldown.
 *
 * @see Cooldown
 * @since 2.0.0
 */
public class CooldownDefinition {

    private long delay;
    private TimeUnit timeUnit;

    private CooldownDefinition(long delay, TimeUnit timeUnit) {
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    /**
     * Builds a new CooldownDefinition.
     *
     * @param cooldown an instance of the corresponding {@link Cooldown} annotation
     * @return a new CooldownDefinition
     */
    @NotNull
    public static CooldownDefinition build(@Nullable Cooldown cooldown) {
        if (cooldown == null) {
            return new CooldownDefinition(0, TimeUnit.MILLISECONDS);
        }
        return new CooldownDefinition(cooldown.value(), cooldown.timeUnit());
    }

    /**
     * Sets the corresponding {@link Cooldown} annotation.
     *
     * @param cooldown the new {@link Cooldown} annotation to use
     */
    public void set(@Nullable CooldownDefinition cooldown) {
        if (cooldown == null) {
            delay = 0;
            return;
        }
        delay = cooldown.delay;
        timeUnit = cooldown.timeUnit;
    }

    /**
     * Gets the cooldown delay.
     *
     * @return the cooldown delay
     */
    public long getDelay() {
        return delay;
    }

    /**
     * Sets the cooldown delay
     *
     * @param delay the new delay
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    /**
     * Gets the {@link TimeUnit} of the cooldown.
     *
     * @return the {@link TimeUnit} of the cooldown
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    /**
     * Sets the {@link TimeUnit} of the cooldown.
     *
     * @param timeUnit the new {@link TimeUnit}
     */
    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    @Override
    public String toString() {
        return "{" + "delay=" + delay + ", timeUnit=" + timeUnit + '}';
    }
}
