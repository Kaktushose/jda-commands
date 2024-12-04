package com.github.kaktushose.jda.commands.dispatching.middleware.impl;

import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Middleware} implementation that will check permissions.
 * The default implementation can only handle discord permissions. However, the {@link PermissionsProvider} can be
 * used for own implementations.
 * This filter will first check against {@link PermissionsProvider#hasPermission(User, Context)} with a
 * {@link User} object. This can be used for global permissions. Afterward
 * {@link PermissionsProvider#hasPermission(Member, Context)} will be called. Since the {@link Member} is
 * available this might be used for guild related permissions.
 *
 * @see Permissions
 * @see PermissionsProvider
 * @since 2.0.0
 */
public class PermissionsMiddleware implements Middleware {

    private static final Logger log = LoggerFactory.getLogger(PermissionsMiddleware.class);

    /**
     * Checks if the {@link User} and respectively the {@link Member} has the permission to execute the command.
     *
     * @param context the {@link Context} to filter
     */
    @Override
    public void accept(@NotNull Context context) {
        log.debug("Checking permissions...");
        PermissionsProvider provider = context.getImplementationRegistry().getPermissionsProvider();
        GenericInteractionCreateEvent event = context.getEvent();

        boolean hasPerms;
        if (event.getMember() != null) {
            hasPerms = provider.hasPermission(event.getMember(), context);
        } else {
            hasPerms = provider.hasPermission(event.getUser(), context);
        }
        if (!hasPerms) {
            context.setCancelled(context.getImplementationRegistry().getErrorMessageFactory().getInsufficientPermissionsMessage(context));
            log.debug("Insufficient permissions!");
            return;
        }

        log.debug("All permission checks passed");
    }
}
