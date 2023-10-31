package com.github.kaktushose.jda.commands.dispatching.filter.impl;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.dispatching.filter.Filter;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.SlashCommandContext;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Filter} implementation that checks if a
 * {@link SlashCommandDefinition} is available for execution in direct messages.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see SlashCommand#isGuildOnly()
 * @since 2.0.0
 */
public class DirectMessageFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(DirectMessageFilter.class);

    /**
     * Checks if a {@link SlashCommandDefinition} is available for execution in
     * direct messages and if not cancels the {@link Context},
     *
     * @param context the {@link Context} to filter
     */
    @Override
    public void apply(@NotNull Context context) {
        SlashCommandContext commandContext = (SlashCommandContext) context;
        Channel channel = context.getEvent().getChannel();
        if (channel == null) {
            return;
        }
        if (channel.getType().equals(ChannelType.PRIVATE) && !commandContext.getCommand().isGuildOnly()) {
            log.debug("Received private message but command cannot be executed in DMs!");
            context.setCancelled(true).setErrorMessage(
                    context.getImplementationRegistry().getErrorMessageFactory().getWrongChannelTypeMessage(context)
            );
        }
    }
}
