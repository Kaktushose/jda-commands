package com.github.kaktushose.jda.commands.dispatching.filter.impl;

import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.filter.Filter;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Filter} implementation that checks if a
 * {@link com.github.kaktushose.jda.commands.reflect.CommandDefinition} is available for execution in direct messages.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see Command#isDM()
 * @since 2.0.0
 */
public class DirectMessageFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(DirectMessageFilter.class);

    /**
     * Checks if a {@link com.github.kaktushose.jda.commands.reflect.CommandDefinition} is available for execution in
     * direct messages and if not cancels the {@link CommandContext},
     *
     * @param context the {@link CommandContext} to filter
     */
    @Override
    public void apply(@NotNull CommandContext context) {
        if (context.getEvent().isFromType(ChannelType.PRIVATE) && !context.getCommand().isDM()) {
            log.debug("Received private message but command cannot be executed in DMs!");
            context.setCancelled(true);
            context.setErrorMessage(context.getImplementationRegistry().getErrorMessageFactory().getWrongChannelTypeMessage(context));
        }
    }
}
