package io.github.kaktushose.jdac.dispatching.middleware.impl;

import io.github.kaktushose.jdac.annotations.interactions.Cooldown;
import io.github.kaktushose.jdac.definitions.interactions.command.SlashCommandDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.SlashCommandDefinition.CooldownDefinition;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/// A [Middleware] implementation that contains the business logic behind command cooldowns.
/// If the command isn't annotated with [Cooldown] or more
/// formally if the [SlashCommandDefinition] doesn't hold a [CooldownDefinition] or the delay of the
/// [CooldownDefinition] amounts to `0` this filter has no effect.
///
/// @see Cooldown
public class CooldownMiddleware implements Middleware {

    private static final Logger log = LoggerFactory.getLogger(CooldownMiddleware.class);
    private final Map<Long, Set<CooldownEntry>> activeCooldowns = new ConcurrentHashMap<>();

    private final ErrorMessageFactory errorMessageFactory;

    public CooldownMiddleware(ErrorMessageFactory errorMessageFactory) {
        this.errorMessageFactory = errorMessageFactory;
    }

    /// Checks if an active cooldown for the given [SlashCommandDefinition] exists and will eventually cancel the
    /// context.
    ///
    /// @param context the [InvocationContext] to filter
    @Override
    public void accept(InvocationContext<?> context) {
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
                context.cancel(errorMessageFactory.getCooldownMessage(context, remaining));
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
