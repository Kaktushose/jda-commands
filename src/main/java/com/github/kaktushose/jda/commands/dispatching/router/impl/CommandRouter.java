package com.github.kaktushose.jda.commands.dispatching.router.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.router.Router;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * An implementation of {@link Router} that works for message based input.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see com.github.kaktushose.jda.commands.dispatching.router.Router
 * @since 2.0.0
 */
public class CommandRouter implements Router {

    @Override
    public void findCommands(@NotNull CommandContext context, @NotNull Collection<CommandDefinition> commands) {
        for (int i = 0; i < context.getSettings().getMaxDistance(); i++) {
            if (findCommand(context, commands, i)) {
                return;
            }
        }
        if (context.getCommand() == null || context.isCancelled()) {
            context.setErrorMessage(context.getImplementationRegistry()
                    .getErrorMessageFactory()
                    .getCommandNotFoundMessage(context)
            );
            context.setCancelled(true);
        }
    }

    private boolean findCommand(CommandContext context, Collection<CommandDefinition> commands, int maxDistance) {
        CommandDefinition command = null;
        boolean success = false;
        String[] input = context.getInput();
        AtomicInteger matchingLength = new AtomicInteger(0);
        for (int i = input.length - 1; i > -1; i--) {

            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < i + 1; j++) {
                sb.append(input[j]).append(" ");
            }
            String generatedLabel = sb.toString().trim();
            List<CommandDefinition> possibleCommands = commands.stream().filter(cmd -> cmd.getLabels().stream().anyMatch(label -> {
                        String[] expectedLabels = label.split(" ");
                        String[] actualLabels = generatedLabel.split(" ");

                        if (expectedLabels.length != actualLabels.length) {
                            return false;
                        }

                        boolean matches = true;
                        for (int k = 0; k < expectedLabels.length; k++) {
                            if (!matches) {
                                return false;
                            }

                            boolean ignoreCase = context.getSettings().isIgnoreCase();
                            String expected = ignoreCase ? expectedLabels[k].toUpperCase() : expectedLabels[k];
                            String actual = ignoreCase ? actualLabels[k].toUpperCase() : actualLabels[k];
                            if (maxDistance == 0) {
                                matches = expected.startsWith(actual);
                            } else {
                                matches = calculateLevenshteinDistance(expected, actual) <= maxDistance;
                            }

                            if (matches) {
                                matchingLength.set(actualLabels.length);
                            }
                        }
                        return matches;
                    })
            ).collect(Collectors.toList());

            if (possibleCommands.size() == 1) {
                command = possibleCommands.get(0);
                success = true;
                break;
            }
            if (possibleCommands.size() > 1) {
                for (CommandDefinition possible : possibleCommands) {
                    if (possible.getLabels().contains(generatedLabel)) {
                        command = possible;
                        success = true;
                        break;
                    }
                }
                if (!success) {
                    context.setPossibleCommands(possibleCommands);
                    context.setCancelled(true);
                    break;
                }
            }
        }
        context.setInput(Arrays.copyOfRange(input, matchingLength.get(), input.length));
        context.setCommand(command);
        return success;
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
