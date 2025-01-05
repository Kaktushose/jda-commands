package com.github.kaktushose.jda.commands.dispatching.middleware.impl;

import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.dispatching.ImplementationRegistry;
import com.github.kaktushose.jda.commands.dispatching.context.InvocationContext;
import com.github.kaktushose.jda.commands.dispatching.middleware.Middleware;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// A [Middleware] implementation that will check permissions.
/// The default implementation can only handle discord permissions. However, the [PermissionsProvider] can be
/// used for own implementations.
/// This filter will first check against [#hasPermission(User,InvocationContext)] with a
/// [User] object. This can be used for global permissions. Afterward
/// [#hasPermission(User,InvocationContext)] will be called. Since the [Member] is
/// available this might be used for guild related permissions.
///
/// @see Permissions
/// @see PermissionsProvider
public class PermissionsMiddleware implements Middleware {

    private static final Logger log = LoggerFactory.getLogger(PermissionsMiddleware.class);

    private final ImplementationRegistry implementationRegistry;

    public PermissionsMiddleware(ImplementationRegistry implementationRegistry) {
        this.implementationRegistry = implementationRegistry;
    }

    /// Checks if the [User] and respectively the [Member] has the permission to execute the command.
    ///
    /// @param context the [InvocationContext] to filter
    @Override
    public void accept(@NotNull InvocationContext<?> context) {
        log.debug("Checking permissions...");
        PermissionsProvider provider = implementationRegistry.getPermissionsProvider();
        GenericInteractionCreateEvent event = context.event();

        boolean hasPerms;
        if (event.getMember() != null) {
            hasPerms = provider.hasPermission(event.getMember(), context);
        } else {
            hasPerms = provider.hasPermission(event.getUser(), context);
        }
        if (!hasPerms) {
            context.cancel(implementationRegistry.getErrorMessageFactory().getInsufficientPermissionsMessage(context));
            log.debug("Insufficient permissions!");
            return;
        }

        log.debug("All permission checks passed");
    }
}
