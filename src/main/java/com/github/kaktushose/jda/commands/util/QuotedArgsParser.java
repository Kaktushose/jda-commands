package com.github.kaktushose.jda.commands.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class QuotedArgsParser {
    private static final Collection<Character> QUOTATION_MARKS = Arrays.asList('\'', '\"');
    private static final Character SPACE = ' ';

    /**
     * @author stijnb1234
     */
    public static List<String> parseArguments(String raw) {
        char[] chars = raw.toCharArray();

        List<String> arguments = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        boolean quote = false;
        int i = 0;
        for (char c : chars) {
            // Increment i
            i++;

            // If we're not in quotes and there's a space, a word is finished
            if (!quote && c == SPACE) {
                arguments.add(sb.toString().trim());
                sb.setLength(0);
                continue;
            }

            // If char is a quotation mark, then reverse booleans
            if (QUOTATION_MARKS.contains(c)) {
                quote = !quote;
                continue;
            }

            // Append character
            sb.append(c);

            // If at last word, there's not expected to be a final space
            if (i >= chars.length) {
                arguments.add(sb.toString().trim());
                sb.setLength(0);
            }
        }

        // Finally, return the arguments
        return arguments;
    }
}
