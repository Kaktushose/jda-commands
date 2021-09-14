package com.github.kaktushose.jda.commands.dispatching.filter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.filter.Filter;
import net.dv8tion.jda.api.MessageBuilder;
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
            context.setErrorMessage(new MessageBuilder().append("not executable in dms").build());
        }
    }
}
