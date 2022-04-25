package com.github.kaktushose.jda.commands.dispatching.slash;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class HelpAutoCompleteListener extends ListenerAdapter {

    private Set<String> labels;

    public HelpAutoCompleteListener() {
        this.labels = new HashSet<>();
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

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }
}
