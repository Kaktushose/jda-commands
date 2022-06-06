package com.github.kaktushose.jda.commands.dispatching.filter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.GenericEvent;
import com.github.kaktushose.jda.commands.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Filter} implementation that will check permissions.
 * The default implementation can only handle discord permissions. However, the {@link PermissionsProvider} can be
 * used for own implementations.
 * This filter will first check against {@link PermissionsProvider#hasPermission(User, CommandContext)} with a
 * {@link User} object. This can be used for global permissions. Afterwards
 * {@link PermissionsProvider#hasPermission(Member, CommandContext)} will be called. Since the {@link Member} is
 * available this might be used for guild related permissions.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see com.github.kaktushose.jda.commands.annotations.Permission
 * @see PermissionsProvider
 * @since 2.0.0
 */
public class PermissionsFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(PermissionsFilter.class);

    /**
     * Checks if the {@link User} and respectively the {@link Member} has the permission to execute the command.
     *
     * @param context the {@link CommandContext} to filter
     */
    @Override
    public void apply(@NotNull CommandContext context) {
        log.debug("Checking permissions...");
        PermissionsProvider provider = context.getImplementationRegistry().getPermissionsProvider();

        GenericEvent event = context.getEvent();

        boolean isCancelled = !provider.hasPermission(event.getAuthor(), context);

        // we only have member information in a guild channel
        if (!isCancelled && event.isFromGuild()) {
            isCancelled = !provider.hasPermission(event.getMember(), context);
        }

        if (isCancelled) {
            context.setCancelled(true);
            context.setErrorMessage(context
                    .getImplementationRegistry()
                    .getErrorMessageFactory()
                    .getInsufficientPermissionsMessage(context)
            );
            log.debug("Insufficient permissions!");
            return;
        }

        log.debug("All permission checks passed");
    }
}
