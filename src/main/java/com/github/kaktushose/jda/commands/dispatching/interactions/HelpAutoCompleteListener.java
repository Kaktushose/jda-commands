package com.github.kaktushose.jda.commands.dispatching.interactions;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Listener providing auto complete for the help command. This listener will only be active if more than 25 commands
 * are available. Otherwise, command options are used.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @since 2.3.0
 */
public class HelpAutoCompleteListener extends ListenerAdapter {

    private Collection<String> labels;

    /**
     * Constructs a new HelpAutoCompleteListener.
     */
    public HelpAutoCompleteListener() {
        this.labels = new ArrayList<>();
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("help")) {
            event.replyChoices(
                    labels.stream().filter(label -> label.startsWith(event.getFocusedOption().getValue()))
                            .map(word -> new Command.Choice(word, word))
                            .collect(Collectors.toList())
            ).queue();
        }

    }

    /**
     * Sets the labels to use for auto complete.
     *
     * @param labels the labels to use for auto complete
     */
    public void setLabels(@NotNull Collection<String> labels) {
        this.labels = labels;
    }
}
