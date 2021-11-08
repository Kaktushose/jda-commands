package com.github.kaktushose.jda.commands.dispatching.parser.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.CommandDispatcher;
import com.github.kaktushose.jda.commands.dispatching.parser.Parser;
import com.github.kaktushose.jda.commands.embeds.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.entities.ChannelType;
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
    public CommandContext parse(MessageReceivedEvent event, CommandDispatcher dispatcher) {
        CommandContext context = new CommandContext();
        ImplementationRegistry registry = dispatcher.getImplementationRegistry();
        GuildSettings settings = registry.getSettingsProvider().getSettings(event.isFromType(ChannelType.TEXT) ? event.getGuild() : null);
        ErrorMessageFactory errorMessageFactory = registry.getErrorMessageFactory();

        context.setEvent(event)
                .setSettings(settings)
                .setJdaCommands(dispatcher.getJdaCommands())
                .setImplementationRegistry(registry);

        if (event.getAuthor().isBot() && settings.isIgnoreBots()) {
            return context.setCancelled(true);
        }

        if (settings.isMutedGuild()) {
            context.setErrorMessage(errorMessageFactory.getGuildMutedMessage(context));
            return context.setCancelled(true);
        }

        if (settings.getMutedChannels().contains(event.getChannel().getIdLong())) {
            context.setErrorMessage(errorMessageFactory.getChannelMutedMessage(context));
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
            boolean isQuote = false;
            char[] chars = contentRaw.toCharArray();
            List<String> arguments = new ArrayList<>();

            for (int i = 0; i < chars.length; i++) {
                if (QUOTATION_MARKS.contains(chars[i])) {
                    isQuote = !isQuote;
                    continue;
                }

                builder.append(chars[i]);

                if (isQuote) {
                    if (i == chars.length - 1) {
                        arguments.add(builder.toString().trim());
                        builder.setLength(0);
                    }
                } else {
                    if (SPACE.equals(chars[i]) || i == chars.length - 1) {
                        arguments.add(builder.toString().trim());
                        builder.setLength(0);
                    }
                }
            }
            input = arguments.toArray(new String[0]);
        }

        return context.setInput(input);
    }
}
