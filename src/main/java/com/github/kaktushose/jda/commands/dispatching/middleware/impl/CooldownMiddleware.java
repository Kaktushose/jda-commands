package com.github.kaktushose.jda.commands.dispatching.middleware.impl;

import com.github.kaktushose.jda.commands.annotations.interactions.Cooldown;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.SlashCommandContext;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.reflect.CooldownDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A {@link Middleware} implementation that contains the business logic behind command cooldowns.
 * If the command isn't annotated with {@link Cooldown Cooldown} or more
 * formally if the {@link SlashCommandDefinition} doesn't hold a {@link CooldownDefinition} or the delay of the
 * {@link CooldownDefinition} amounts to {@code 0} this filter has no effect.
 *
 * @see Cooldown
 * @since 2.0.0
 */
public class CooldownMiddleware implements Middleware {

    private static final Logger log = LoggerFactory.getLogger(CooldownMiddleware.class);
    private final Map<Long, Set<CooldownEntry>> activeCooldowns;

    public CooldownMiddleware() {
        activeCooldowns = new HashMap<>();
    }

    /**
     * Checks if an active cooldown for the given {@link SlashCommandDefinition} exists and will eventually cancel the
     * context.
     *
     * @param context the {@link Context} to filter
     */
    @Override
    public void accept(@NotNull Context context) {
        if (!SlashCommandInteractionEvent.class.isAssignableFrom(context.getEvent().getClass())) {
            return;
        }
        SlashCommandDefinition command = ((SlashCommandContext) context).getCommand();

        if (!command.hasCooldown()) {
            return;
        }

        long id = context.getEvent().getUser().getIdLong();

        activeCooldowns.putIfAbsent(id, new HashSet<>());

        Optional<CooldownEntry> optional = activeCooldowns.get(id).stream().filter(entry -> entry.command.equals(command)).findFirst();

        if (optional.isPresent()) {
            CooldownEntry entry = optional.get();
            long remaining = entry.duration - (System.currentTimeMillis() - entry.startTime);
            if (remaining <= 0) {
                activeCooldowns.get(id).remove(entry);
            } else {
                context.setCancelled(context.getImplementationRegistry().getErrorMessageFactory().getCooldownMessage(context, remaining));
                log.debug("Command has a remaining cooldown of {} ms!", remaining);
                return;
            }
        }

        CooldownDefinition cooldown = command.getCooldown();
        long startTime = System.currentTimeMillis();
        long duration = cooldown.timeUnit().toMillis(cooldown.delay());
        activeCooldowns.get(id).add(new CooldownEntry(command, startTime, duration));
        log.debug("Added new cooldown entry for this user");
    }

    private static class CooldownEntry {
        private final SlashCommandDefinition command;
        private final long startTime;
        private final long duration;

        public CooldownEntry(SlashCommandDefinition command, long startTime, long duration) {
            this.command = command;
            this.startTime = startTime;
            this.duration = duration;
        }

        public SlashCommandDefinition getCommand() {
            return command;
        }

        public long getStartTime() {
            return startTime;
        }

        public long getDuration() {
            return duration;
        }
    }
}
