package com.github.kaktushose.jda.commands.rewrite.dispatching.filter.impl;

import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.rewrite.dispatching.filter.Filter;
import net.dv8tion.jda.api.entities.ChannelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectMessageFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(DirectMessageFilter.class);

    @Override
    public void apply(CommandContext context) {
        if (context.getEvent().isFromType(ChannelType.PRIVATE) && !context.getCommand().isDM()) {
            log.debug("Received private message but command cannot be executed in DMs!");
            context.setCancelled(true);
        }
    }
}
