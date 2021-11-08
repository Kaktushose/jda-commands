package com.github.kaktushose.jda.commands.dispatching.filter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionsFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(PermissionsFilter.class);

    @Override
    public void apply(CommandContext context) {
        log.debug("Checking permissions...");
        PermissionsProvider provider = context.getImplementationRegistry().getPermissionsProvider();

        MessageReceivedEvent event = context.getEvent();

        boolean isCancelled = !provider.hasPermission(event.getAuthor(), context);

        // we only have member information in a guild channel
        if (!isCancelled && event.isFromType(ChannelType.TEXT)) {
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
