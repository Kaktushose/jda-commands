package com.github.kaktushose.jda.commands.rewrite.reflect;

import com.github.kaktushose.jda.commands.rewrite.annotations.Cooldown;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public class CooldownDefinition {

    private long delay;
    private TimeUnit timeUnit;

    private CooldownDefinition(long delay, TimeUnit timeUnit) {
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    public static CooldownDefinition build(@Nullable Cooldown cooldown) {
        if (cooldown == null) {
            return new CooldownDefinition(0, TimeUnit.MILLISECONDS);
        }
        return new CooldownDefinition(cooldown.value(), cooldown.timeUnit());
    }

    public void set(@Nullable CooldownDefinition cooldown) {
        if (cooldown == null) {
            delay = 0;
            return;
        }
        delay = cooldown.delay;
        timeUnit = cooldown.timeUnit;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }
}
