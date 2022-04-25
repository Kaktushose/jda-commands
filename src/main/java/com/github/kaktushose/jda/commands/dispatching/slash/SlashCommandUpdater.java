package com.github.kaktushose.jda.commands.dispatching.slash;

import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SlashCommandUpdater {

    private static final Logger log = LoggerFactory.getLogger(SlashCommandUpdater.class);
    private final JDA jda;
    private final SlashConfiguration configuration;

    public SlashCommandUpdater(JDA jda, SlashConfiguration configuration) {
        this.jda = jda;
        this.configuration = configuration;
    }

    public void update(Set<CommandDefinition> commands) {
        Set<CommandData> commandData = new HashSet<>();
        Set<String> labels = new HashSet<>();
        for (CommandDefinition command : commands) {
            try {
                commandData.add(command.toCommandData());
                labels.add(command.getLabels().get(0));
            } catch (Exception e) {
                log.error(String.format("Failed to update command %s.%s!",
                        command.getMethod().getDeclaringClass().getSimpleName(),
                        command.getMethod().getName()
                ), new InvocationTargetException(e, "Invalid slash command signature!"));
            }
        }

        commandData.add(Commands.slash("commands", "Get an overview over all available commands"));
        OptionData optionData = new OptionData(OptionType.STRING, "command", "the command you need help with");
        if (labels.size() > 25) {
            optionData.setAutoComplete(true);
        } else {
            optionData.addChoices(labels.stream().map(s -> new Command.Choice(s, s)).collect(Collectors.toList()));
        }
        commandData.add(Commands.slash("help", "Get specific help for commands").addOptions(optionData));

        if (configuration.isGlobal()) {
            jda.updateCommands().addCommands(commandData).queue();
        } else {
            configuration.getGuildIds()
                    .stream()
                    .map(id -> Optional.ofNullable(jda.getGuildById(id)))
                    .filter(Optional::isPresent)
                    .forEach(guild -> guild.get().updateCommands().addCommands(commandData).queue());
        }
    }
}
