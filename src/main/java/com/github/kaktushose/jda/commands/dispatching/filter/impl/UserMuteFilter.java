package com.github.kaktushose.jda.commands.dispatching.filter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserMuteFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(UserMuteFilter.class);

    @Override
    public void apply(CommandContext context) {
        log.debug("Checking mutes...");
        PermissionsProvider provider = context.getImplementationRegistry().getPermissionsProvider();

        if (provider.isMuted(context.getEvent().getAuthor(), context)) {
            context.setCancelled(true);
            context.setErrorMessage(context
                    .getImplementationRegistry()
                    .getErrorMessageFactory()
                    .getUserMutedMessage(context)
            );
            log.debug("Insufficient permissions - User is muted!");
            return;
        }

        log.debug("All mute checks passed");
    }
}
