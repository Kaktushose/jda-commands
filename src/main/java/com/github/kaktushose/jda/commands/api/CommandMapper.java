package com.github.kaktushose.jda.commands.api;

import com.github.kaktushose.jda.commands.entities.CommandCallable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The default command mapper of this framework.
 *
 * @author Kaktushose
 * @version 1.0.0
 * @since 1.0.0
 */
public class CommandMapper {

    /**
     * Attempts to find the {@link CommandCallable} that is mapped to a label which is generated through the input.
     * <p>This command mapper supports aliases as well as a feature called command shortening. Command shortening
     * essentially means that the input doesn't need to be equals to the label. Instead it is sufficient if the input starts with
     * the label. For instance if the label is <em>foo</em>, then <em>f</em>, <em>fo</em> and <em>foo</em> are all valid input.
     * If more then one command can be mapped the command mapping fails.
     *
     * @param commands          the List of {@link CommandCallable}s to search through
     * @param input             the input to build the labels from
     * @param isLabelIgnoreCase true if the case of the input should be ignored
     * @return an Optional describing the matching {@link CommandCallable} or an empty Optional if no command was found.
     */
    public Optional<CommandCallable> findCommand(@Nonnull List<CommandCallable> commands, @Nonnull String[] input, boolean isLabelIgnoreCase) {
        Optional<CommandCallable> command = Optional.empty();
        for (int i = input.length - 1; i > -1; i--) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < i + 1; j++) {
                sb.append(input[j]).append(" ");
            }
            String generatedLabel = isLabelIgnoreCase ? sb.toString().toLowerCase().trim() : sb.toString().trim();
            List<CommandCallable> possibleCommands = commands.stream().filter(cmd -> cmd.getLabels().stream().anyMatch(label -> {
                        boolean matches = true;
                        String[] expectedLabels = label.split(" ");
                        String[] actualLabels = generatedLabel.split(" ");
                        if (expectedLabels.length != actualLabels.length) {
                            return false;
                        }
                        for (int k = 0; k < expectedLabels.length; k++) {
                            if (!matches) {
                                return false;
                            }
                            matches = expectedLabels[k].startsWith(actualLabels[k]);
                        }
                        return matches;
                    })
            ).collect(Collectors.toList());
            if (possibleCommands.size() == 1) {
                return possibleCommands.stream().findFirst();
            }
            if (possibleCommands.size() > 0) {
                return Optional.empty();
            }
        }
        return command;
    }

}
