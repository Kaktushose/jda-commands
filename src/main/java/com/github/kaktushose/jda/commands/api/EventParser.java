package com.github.kaktushose.jda.commands.api;

import com.github.kaktushose.jda.commands.entities.CommandCallable;
import com.github.kaktushose.jda.commands.entities.CommandSettings;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * The default event parser of this framework.
 *
 * @author Kaktushose
 * @version 1.0.0
 * @since 1.0.0
 */
public class EventParser {

    /**
     * boolean describing if a bot mention was used as prefix
     */
    private boolean eventIsBotMention;

    /**
     * Checks if a {@code GuildMessageReceivedEvent} matches all requirements given by a {@link CommandSettings} object.
     * This method gets invoked on every incoming {@code GuildMessageReceivedEvent}.
     *
     * @param event    the corresponding {@code GuildMessageReceivedEvent}
     * @param settings the {@link CommandSettings}
     * @return {@code true} if the event is valid
     */
    public boolean validateEvent(GuildMessageReceivedEvent event, CommandSettings settings) {
        if (settings.isIgnoreBots() && event.getAuthor().isBot()) {
            return false;
        }
        if (settings.getMutedChannels().contains(event.getChannel().getIdLong())) {
            return false;
        }
        if (settings.getMutedUsers().contains(event.getAuthor().getIdLong())) {
            return false;
        }
        if (event.getMessage().getContentDisplay().startsWith(settings.getGuildPrefix(event.getGuild()))) {
            eventIsBotMention = false;
            return true;
        }
        if (event.getMessage().getMentionedUsers().size() > 0) {
            eventIsBotMention = settings.isBotMentionPrefix() && event.getMessage().getMentionedMembers().get(0).getUser().equals(event.getJDA().getSelfUser());
            return eventIsBotMention;
        }
        return false;
    }

    /**
     * Parses an {@code GuildMessageReceivedEvent} by removing whitespaces and the prefix from the message and then
     * splitting it at each blank space. This method gets invoked if an event is valid and thus ready to be processed.
     *
     * @param event    the corresponding {@code GuildMessageReceivedEvent}
     * @param settings the {@link CommandSettings}
     * @return the split user input
     */
    public String[] parseEvent(GuildMessageReceivedEvent event, CommandSettings settings) {
        String contentRaw = event.getMessage().getContentRaw();
        while (contentRaw.contains("  ")) {
            contentRaw = contentRaw.replaceAll(" {2}", " ");
        }
        contentRaw = contentRaw.replaceFirst(Pattern.quote(settings.getGuildPrefix(event.getGuild())), "").trim();
        String[] split = contentRaw.split(" ");
        return eventIsBotMention ? Arrays.copyOfRange(split, 1, split.length) : split;
    }

    /**
     * Checks if the {@code GuildMessageReceivedEvent} or respectively the message author has the required permissions to
     * execute the command.
     *
     * @param commandCallable the {@link CommandCallable} to check
     * @param event           the corresponding {@code GuildMessageReceivedEvent}
     * @param settings        the {@link CommandSettings}
     * @return {@code true} if the event author has to required permissions
     */
    public boolean hasPermission(CommandCallable commandCallable, GuildMessageReceivedEvent event, CommandSettings settings) {
        return commandCallable.getPermissions().stream().allMatch(permission -> settings.getPermissionHolders(permission).contains(event.getAuthor().getIdLong()));
    }

}
