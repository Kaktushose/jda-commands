package com.github.kaktushose.jda.commands.dispatching.parser.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.parser.Parser;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import com.github.kaktushose.jda.commands.settings.SettingsProvider;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class DefaultMessageParser extends Parser<MessageReceivedEvent> {

    private static final Collection<Character> QUOTATION_MARKS = Arrays.asList('\'', '\"');
    private static final Character SPACE = ' ';

    @Override
    public CommandContext parse(MessageReceivedEvent event, SettingsProvider settingsProvider) {
        CommandContext context = new CommandContext();
        GuildSettings settings = settingsProvider.getSettings(event.getGuild());

        if (event.getAuthor().isBot() && settings.isIgnoreBots()) {
            return context.setCancelled(true);
        }

        if (settings.isMutedGuild()) {
            return context.setCancelled(true);
        }

        if (settings.getMutedChannels().contains(event.getChannel().getIdLong())) {
            return context.setCancelled(true);
        }

        String contentRaw = event.getMessage().getContentRaw();

        while (contentRaw.contains("  ")) {
            contentRaw = contentRaw.replaceAll(" {2}", " ");
        }

        if (!contentRaw.startsWith(settings.getPrefix())) {
            return context.setCancelled(true);
        }

        contentRaw = contentRaw.replaceFirst(Pattern.quote(settings.getPrefix()), "").trim();
        String[] input = contentRaw.split(" ");

        /*
         * Splits the raw content at every space but will also concatenate the Strings inside a quote.
         * E.g. Hello "Foo Bar" World -> [Hello, Foo Bar, World]
         *
         * @author stijnb1234
         */
        if (settings.isParseQuotes()) {
            StringBuilder builder = new StringBuilder();
            boolean quote = false;
            int i = 0;
            char[] chars = contentRaw.toCharArray();
            List<String> arguments = new ArrayList<>();
            for (char c : chars) {
                i++;

                // If we're not in quotes and there's a space, a word is finished
                if (!quote && c == SPACE) {
                    arguments.add(builder.toString().trim());
                    builder.setLength(0);
                    continue;
                }

                // If char is a quotation mark, then reverse booleans
                if (QUOTATION_MARKS.contains(c)) {
                    quote = !quote;
                    continue;
                }

                builder.append(c);

                // If at last word, there's not expected to be a final space
                if (i >= chars.length) {
                    arguments.add(builder.toString().trim());
                    builder.setLength(0);
                }
            }
            input = arguments.toArray(new String[0]);
        }

        return context.setInput(input).setEvent(event).setSettings(settings);
    }
}
