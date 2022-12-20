package com.github.kaktushose.jda.commands.dispatching.parser.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.CommandDispatcher;
import com.github.kaktushose.jda.commands.dispatching.parser.Parser;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * An implementation of {@link Parser} that can parse {@link MessageReceivedEvent MessageReceivedEvents}.
 * This parser will work within the limitations given by the {@link GuildSettings}.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public class DefaultMessageParser extends Parser<MessageReceivedEvent> {

    private static final Collection<Character> QUOTATION_MARKS = Arrays.asList('\'', '\"');
    private static final Character SPACE = ' ';

    /**
     * Takes a {@link MessageReceivedEvent}, parses and transpiles it into a {@link CommandContext}.
     *
     * @param event      the {@link MessageReceivedEvent} to parse
     * @param dispatcher the calling {@link CommandDispatcher}
     * @return a new {@link CommandContext}
     */
    @Override
    public CommandContext parse(@NotNull MessageReceivedEvent event, @NotNull CommandDispatcher dispatcher) {
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

        if (isHelpLabel(context, input[0])) {
            context.setInput(Arrays.copyOfRange(input, 1, input.length));
            context.setHelpEvent(true);
        } else {
            context.setInput(input);
        }

        return context;
    }

    private boolean isHelpLabel(CommandContext context, String input) {
        for (int i = 0; i < context.getSettings().getMaxDistance(); i++) {
            if (findHelpLabel(context, input, i)) {
                return true;
            }
        }
        return false;
    }

    private boolean findHelpLabel(CommandContext context, String input, int maxDistance) {
        if (maxDistance == 0) {
            return context.getSettings().getHelpLabels().stream().anyMatch(label -> label.startsWith(input));
        }
        return context.getSettings().getHelpLabels().stream().anyMatch(
                label -> calculateLevenshteinDistance(label, input) <= maxDistance
        );
    }

    private int calculateLevenshteinDistance(String first, String second) {
        int[][] dp = new int[first.length() + 1][second.length() + 1];
        for (int i = 0; i <= first.length(); i++) {
            for (int j = 0; j <= second.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(
                            dp[i - 1][j - 1] + costOfSubstitution(first.charAt(i - 1), second.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1
                    );
                }
            }
        }
        return dp[first.length()][second.length()];
    }

    private int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }

}
