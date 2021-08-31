package com.github.kaktushose.jda.commands.rewrite.dispatching.filter.impl;

import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.rewrite.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.rewrite.reflect.CommandDefinition;
import com.github.kaktushose.jda.commands.rewrite.reflect.CooldownDefinition;

import java.util.*;

public class CooldownFilter implements Filter {

    private final Map<Long, Set<CooldownEntry>> activeCooldowns;

    public CooldownFilter() {
        activeCooldowns = new HashMap<>();
    }

    @Override
    public void apply(CommandContext context) {
        CommandDefinition command = context.getCommand();

        if (!command.hasCooldown()) {
            return;
        }

        long id = context.getEvent().getAuthor().getIdLong();

        activeCooldowns.putIfAbsent(id, new HashSet<>());

        Optional<CooldownEntry> optional = activeCooldowns.get(id).stream().filter(entry -> entry.command.equals(command)).findFirst();

        if (optional.isPresent()) {
            CooldownEntry entry = optional.get();
            if (System.currentTimeMillis() - entry.startTime >= entry.duration) {
                activeCooldowns.get(id).remove(entry);
            } else {
                context.setCancelled(true);
                return;
            }
        }

        CooldownDefinition cooldown = command.getCooldown();
        long startTime = System.currentTimeMillis();
        long duration = cooldown.getTimeUnit().toMillis(cooldown.getDelay());
        activeCooldowns.get(id).add(new CooldownEntry(command, startTime, duration));
    }

    private static class CooldownEntry {
        private final CommandDefinition command;
        private final long startTime;
        private final long duration;

        public CooldownEntry(CommandDefinition command, long startTime, long duration) {
            this.command = command;
            this.startTime = startTime;
            this.duration = duration;
        }

        public CommandDefinition getCommand() {
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
