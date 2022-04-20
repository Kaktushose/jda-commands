package com.github.kaktushose.jda.commands.slash;

import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

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

        for (CommandDefinition command : commands) {
            try {
                commandData.add(command.toCommandData());
            } catch (Exception e) {
                log.error(String.format("Failed to update command %s.%s!",
                        command.getMethod().getDeclaringClass().getSimpleName(),
                        command.getMethod().getName()
                ), new InvocationTargetException(e, "Invalid slash command signature!"));
            }
        }
        if (configuration.isGlobal()) {
            jda.updateCommands().addCommands(commandData).queue();
        } else {
            configuration.getGuildIds().forEach(id -> {
                Guild guild = jda.getGuildById(id);
                if (guild != null) {
                    guild.updateCommands().addCommands(commandData).queue();
                }
            });
        }
    }
}
