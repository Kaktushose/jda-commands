package io.github.kaktushose.jdac.dispatching.middleware.impl;

import io.github.kaktushose.jdac.annotations.interactions.Permissions;
import io.github.kaktushose.jdac.dispatching.context.InvocationContext;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory;
import io.github.kaktushose.jdac.internal.Helpers;
import io.github.kaktushose.jdac.internal.logging.JDACLogger;
import io.github.kaktushose.jdac.permissions.PermissionsProvider;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.slf4j.Logger;

/// A [Middleware] implementation that will check permissions.
/// The default implementation can only handle discord permissions. However, the [PermissionsProvider] can be
/// used for own implementations.
/// This filter will first check against [PermissionsProvider#hasPermission(User,InvocationContext)] with a
/// [User] object. This can be used for global permissions. Afterward
/// [PermissionsProvider#hasPermission(User,InvocationContext)] will be called. Since the [Member] is
/// available this might be used for guild related permissions.
///
/// @see Permissions
/// @see PermissionsProvider
public class PermissionsMiddleware implements Middleware {

    private static final Logger log = JDACLogger.getLogger(PermissionsMiddleware.class);

    private final PermissionsProvider permissionsProvider;
    private final ErrorMessageFactory errorMessageFactory;

    public PermissionsMiddleware(PermissionsProvider permissionsProvider, ErrorMessageFactory errorMessageFactory) {
        this.permissionsProvider = permissionsProvider;
        this.errorMessageFactory = errorMessageFactory;
    }

    /// Checks if the [User] and respectively the [Member] has the permission to execute the command.
    ///
    /// @param context the [InvocationContext] to filter
    @Override
    public void accept(InvocationContext<?> context) {
        log.debug("Checking permissions...");
        PermissionsProvider provider = permissionsProvider;
        GenericInteractionCreateEvent event = context.event();

        boolean hasPerms;
        if (event.getMember() != null) {
            hasPerms = provider.hasPermission(event.getMember(), context);
        } else {
            hasPerms = provider.hasPermission(event.getUser(), context);
        }
        if (!hasPerms) {
            context.cancel(Helpers.cv2Reply(errorMessageFactory.getInsufficientPermissionsMessage(context)));
            log.debug("Insufficient permissions!");
            return;
        }

        log.debug("All permission checks passed");
    }
}
