package com.github.kaktushose.jda.commands.api;

import com.github.kaktushose.jda.commands.entities.CommandCallable;
import com.github.kaktushose.jda.commands.entities.CommandSettings;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.regex.Pattern;

/**
 * The default event parser of this framework.
 *
 * @author Kaktushose
 * @version 1.1.1
 * @since 1.0.0
 */
public class EventParser {

    /**
     * The prefix used in a command message. Must be stored to sanitize the message later on.
     */
    private String usedPrefix;

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

        String message = event.getMessage().getContentDisplay();
        String prefix = settings.getPrefix();
        if (message.startsWith(prefix) || settings.getPrefixAliases().stream().anyMatch(message::startsWith)) {
            usedPrefix = settings.getPrefixAliases().stream().filter(message::startsWith).findFirst().orElse(prefix);
            return true;
        }

        User selfUser = event.getJDA().getSelfUser();
        List<User> mentionedUsers = event.getMessage().getMentionedUsers();
        if (mentionedUsers.size() > 0) {
            if (mentionedUsers.get(0).equals(selfUser)) {
                usedPrefix = String.format("<@!%s>", selfUser.getId());
                return message.startsWith(usedPrefix);
            }
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
        contentRaw = contentRaw.replaceFirst(Pattern.quote(usedPrefix), "").trim();
        return contentRaw.split(" ");
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

    /**
     * Checks if the {@code Message} starts with the prefix, or it's alternatives in {@link CommandSettings}
     * @param message the {@link String} to check on
     * @param settings the {@link CommandSettings} to use the prefixes from
     * @return {@code true} if the Message starts with a prefix
     */
    public boolean startsWithPrefix(String message, CommandSettings settings) {
        String prefix = settings.getPrefix();
        return message.startsWith(prefix) || settings.getPrefixAliases().stream().anyMatch(message::startsWith);
    }

}
