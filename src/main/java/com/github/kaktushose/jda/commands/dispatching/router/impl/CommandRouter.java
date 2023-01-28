package com.github.kaktushose.jda.commands.dispatching.router.impl;

import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.router.Router;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
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
    public void findCommands(@NotNull GenericContext context, @NotNull Collection<CommandDefinition> commands) {
        if (findCommand(context, commands)) {
            return;
        }
        if (context.getCommand() == null || context.isCancelled()) {
            context.setErrorMessage(context.getImplementationRegistry()
                    .getErrorMessageFactory()
                    .getCommandNotFoundMessage(context)
            );
            context.setCancelled(true);
        }
    }

    private boolean findCommand(GenericContext context, Collection<CommandDefinition> commands) {
        CommandDefinition command = null;
        boolean success = false;
        String[] input = context.getInput();
        AtomicInteger matchingLength = new AtomicInteger(0);
        for (int maxLabelLength = input.length - 1; maxLabelLength > -1; maxLabelLength--) {

            StringBuilder labelBuilder = new StringBuilder();
            for (int index = 0; index < maxLabelLength + 1; index++) {
                labelBuilder.append(input[index]).append(" ");
            }
            String generatedLabel = labelBuilder.toString().trim();
            List<CommandDefinition> possibleCommands = commands.stream().filter(cmd -> cmd.getLabel().stream().anyMatch(label -> {
                        String[] expectedLabels = label.split(" ");
                        String[] actualLabels = generatedLabel.split(" ");

                        if (expectedLabels.length != actualLabels.length) {
                            return false;
                        }

                        boolean matches = true;
                        for (int index = 0; index < expectedLabels.length; index++) {
                            if (!matches) {
                                return false;
                            }
                            boolean ignoreCase = context.getSettings().isIgnoreCase();
                            String expected = ignoreCase ? expectedLabels[index].toUpperCase() : expectedLabels[index];
                            String actual = ignoreCase ? actualLabels[index].toUpperCase() : actualLabels[index];

                            for (int maxDistance = 0; maxDistance < context.getSettings().getMaxDistance(); maxDistance++) {
                                if (maxDistance == 0) {
                                    matches = expected.startsWith(actual);
                                } else {
                                    matches = calculateLevenshteinDistance(expected, actual) <= maxDistance;
                                }
                                if (matches) {
                                    break;
                                }
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
                    if (possible.getLabel().contains(generatedLabel)) {
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
