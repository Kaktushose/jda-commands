package com.github.kaktushose.jda.commands.dispatching.filter.impl;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Filter} implementation that checks if a
 * {@link CommandDefinition} is available for execution in direct messages.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see SlashCommand#isDM()
 * @since 2.0.0
 */
public class DirectMessageFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(DirectMessageFilter.class);

    /**
     * Checks if a {@link CommandDefinition} is available for execution in
     * direct messages and if not cancels the {@link GenericContext},
     *
     * @param context the {@link GenericContext} to filter
     */
    @Override
    public void apply(@NotNull GenericContext context) {
        CommandContext commandContext = (CommandContext) context;
        Channel channel = context.getEvent().getChannel();
        if (channel == null) {
            return;
        }
        if (channel.getType().equals(ChannelType.PRIVATE) && !commandContext.getCommand().isDM()) {
            log.debug("Received private message but command cannot be executed in DMs!");
            context.setCancelled(true);
            // TODO context.setErrorMessage(context.getImplementationRegistry().getErrorMessageFactory().getWrongChannelTypeMessage(context));
        }
    }
}
