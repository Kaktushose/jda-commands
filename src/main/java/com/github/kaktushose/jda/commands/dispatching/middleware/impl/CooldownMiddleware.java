package com.github.kaktushose.jda.commands.dispatching.middleware.impl;

import com.github.kaktushose.jda.commands.annotations.interactions.Cooldown;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.command.SlashCommandDefinition.CooldownDefinition;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.internal.ImplementationRegistry;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/// A [Middleware] implementation that contains the business logic behind command cooldowns.
/// If the command isn't annotated with [Cooldown][Cooldown] or more
/// formally if the [SlashCommandDefinition] doesn't hold a [CooldownDefinition] or the delay of the
/// [CooldownDefinition] amounts to `0` this filter has no effect.
///
/// @see Cooldown
public class CooldownMiddleware implements Middleware {

    private static final Logger log = LoggerFactory.getLogger(CooldownMiddleware.class);
    private final Map<Long, Set<CooldownEntry>> activeCooldowns = new ConcurrentHashMap<>();

    private final ImplementationRegistry implementationRegistry;

    public CooldownMiddleware(ImplementationRegistry implementationRegistry) {
        this.implementationRegistry = implementationRegistry;
    }

    /// Checks if an active cooldown for the given [SlashCommandDefinition] exists and will eventually cancel the
    /// context.
    ///
    /// @param context the [InvocationContext] to filter
    @Override
    public void accept(@NotNull InvocationContext<?> context) {
        if (!(context.definition() instanceof SlashCommandDefinition command) || command.cooldown().delay() <= 0)
            return;

        long id = context.event().getUser().getIdLong();

        activeCooldowns.putIfAbsent(id, new HashSet<>());

        Optional<CooldownEntry> optional = activeCooldowns.get(id).stream().filter(entry -> entry.command.equals(command)).findFirst();

        if (optional.isPresent()) {
            CooldownEntry entry = optional.get();
            long remaining = entry.duration - (System.currentTimeMillis() - entry.startTime);
            if (remaining <= 0) {
                activeCooldowns.get(id).remove(entry);
            } else {
                context.cancel(implementationRegistry.getErrorMessageFactory().getCooldownMessage(remaining));
                log.debug("Command has a remaining cooldown of {} ms!", remaining);
                return;
            }
        }

        CooldownDefinition cooldown = command.cooldown();
        long startTime = System.currentTimeMillis();
        long duration = cooldown.timeUnit().toMillis(cooldown.delay());
        activeCooldowns.get(id).add(new CooldownEntry(command, startTime, duration));
        log.debug("Added new cooldown entry for this user");
    }

    private record CooldownEntry(SlashCommandDefinition command, long startTime, long duration) {
    }
}
