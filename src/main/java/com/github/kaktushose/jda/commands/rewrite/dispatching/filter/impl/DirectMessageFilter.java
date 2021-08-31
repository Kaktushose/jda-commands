package com.github.kaktushose.jda.commands.rewrite.dispatching.filter.impl;

import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.rewrite.dispatching.filter.Filter;
import net.dv8tion.jda.api.entities.ChannelType;

public class DirectMessageFilter implements Filter {

    @Override
    public void apply(CommandContext context) {
        if (context.getEvent().isFromType(ChannelType.PRIVATE) && !context.getCommand().isDM()) {
            context.setCancelled(true);
        }
    }
}
