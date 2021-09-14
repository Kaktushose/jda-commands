package com.github.kaktushose.jda.commands.dispatching.parser.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.parser.Parser;
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
    public CommandContext parse(MessageReceivedEvent event) {
        String contentRaw = event.getMessage().getContentRaw();

        while (contentRaw.contains("  ")) {
            contentRaw = contentRaw.replaceAll(" {2}", " ");
        }

        contentRaw = contentRaw.replaceFirst(Pattern.quote("!"), "").trim();
        String[] input = contentRaw.split(" ");

        /*
         * Splits the raw content at every space but will also concatenate the Strings inside a quote.
         * E.g. Hello "Foo Bar" World -> [Hello, Foo Bar, World]
         *
         * @author stijnb1234
         */
        // TODO make this optional, for the moment it will be default
        if (true) {
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

        return new CommandContext().setInput(input).setEvent(event);
    }
}
