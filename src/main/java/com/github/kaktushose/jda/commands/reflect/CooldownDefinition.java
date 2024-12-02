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
public record CooldownDefinition(
        long delay,
        TimeUnit timeUnit
){
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
}
