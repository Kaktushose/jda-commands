package com.github.kaktushose.jda.commands.rewrite.commands;

import com.github.kaktushose.jda.commands.annotations.Cooldown;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

public class CooldownDefinition {

    private long delay;
    private TimeUnit timeUnit;
    private boolean perUser;

    private CooldownDefinition(long delay, TimeUnit timeUnit, boolean perUser) {
        this.delay = delay;
        this.timeUnit = timeUnit;
        this.perUser = perUser;
    }

    public static CooldownDefinition build(Cooldown cooldown) {
        return new CooldownDefinition(cooldown.value(), cooldown.timeUnit(), cooldown.perUser());
    }

    public void set(@Nullable CooldownDefinition cooldown) {
        if (cooldown == null) {
            delay = 0;
            return;
        }
        delay = cooldown.delay;
        timeUnit = cooldown.timeUnit;
        perUser = cooldown.perUser;
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

    public boolean isPerUser() {
        return perUser;
    }

    public void setPerUser(boolean perUser) {
        this.perUser = perUser;
    }
}
