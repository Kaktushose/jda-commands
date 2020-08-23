package com.github.kaktushose.jda.commands.api;

import com.github.kaktushose.jda.commands.entities.CommandCallable;
import com.github.kaktushose.jda.commands.entities.CommandList;
import com.github.kaktushose.jda.commands.entities.CommandSettings;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * The default help message sender of this framework.
 *
 * @author Kaktushose
 * @version 1.0.0
 * @since 1.0.0
 */
public class HelpMessageSender {

    /**
     * Sends a default help message to a TextChannel.
     * This method gets invoked when a message matches the format {@code prefix<help label>}.
     *
     * @param event        the corresponding {@code GuildMessageReceivedEvent}
     * @param embedFactory a {@link EmbedFactory} to get the default help embed
     * @param settings     the {@link CommandSettings} needed for the {@link EmbedFactory}
     * @param commands     the {@link CommandList} needed for the {@link EmbedFactory}
     */
    public void sendDefaultHelp(GuildMessageReceivedEvent event, EmbedFactory embedFactory, CommandSettings settings, CommandList commands) {
        event.getChannel().sendMessage(embedFactory.getDefaultHelpEmbed(commands, settings, event)).queue();
    }

    /**
     * Sends a specific help message to a TextChannel. T
     * his method gets invoked when a message matches the format {@code prefix<help label> <command label>}.
     *
     * @param event           the corresponding {@code GuildMessageReceivedEvent}
     * @param embedFactory    a {@link EmbedFactory} to get the specific help embed
     * @param settings        the {@link CommandSettings} needed for the {@link EmbedFactory}
     * @param commandCallable the {@link CommandCallable} needed for the {@link EmbedFactory}
     */
    public void sendSpecificHelp(GuildMessageReceivedEvent event, EmbedFactory embedFactory, CommandSettings settings, CommandCallable commandCallable) {
        event.getChannel().sendMessage(embedFactory.getSpecificHelpEmbed(commandCallable, settings, event)).queue();
    }

}
