package com.github.kaktushose.jda.commands.interactions.commands;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.JDAContext;
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.data.slash.CommandTree;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that sends the {@link SlashCommandData} to Discord. Uses a {@link CommandTree} to properly transpile all
 * {@link CommandDefinition CommandDefinitions} to {@link SlashCommandData}.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @see CommandTree
 * @since 2.3.0
 */
public class SlashCommandUpdater {

    private static final Logger log = LoggerFactory.getLogger(SlashCommandUpdater.class);
    private final JDAContext jdaContext;
    private final Collection<CommandDefinition> commands;
    private final GuildScopeProvider guildScopeProvider;

    /**
     * Constructs a new SlashCommandUpdater.
     *
     * @param jdaCommands the corresponding {@link JDACommands} instance
     */
    public SlashCommandUpdater(JDACommands jdaCommands, Collection<CommandDefinition> commands) {
        this.jdaContext = jdaCommands.getJdaContext();
        this.commands = commands;
        guildScopeProvider = jdaCommands.getImplementationRegistry().getGuildScopeProvider();
    }

    /**
     * Sends the {@link SlashCommandData} to Discord. This is equivalent to calling {@link #updateGlobalCommands()} and
     * {@link #updateGuildCommands()} each.
     */
    public void updateAllCommands() {
        updateGuildCommands();
        updateGlobalCommands();
    }

    /**
     * Sends the guild scope {@link SlashCommandData} to Discord.
     */
    public void updateGuildCommands() {
        log.debug("Updating guild slash commands...");
        Set<CommandDefinition> globalCommands = commands.stream()
                .filter(it -> it.getCommandScope() == SlashCommand.CommandScope.GUILD)
                .collect(Collectors.toSet());
        CommandTree tree = new CommandTree(globalCommands);
        log.debug("Generated command tree:\n" + tree);
        Collection<String> labels = tree.getNames();
        log.debug("Using commands: " + labels);
        Map<Long, Set<CommandData>> guildMapping = new HashMap<>();

        for (SlashCommandData command : tree.getCommands()) {
            // create a copy so that a user doesn't modify the command data used for registration
            Set<Long> guildIds = guildScopeProvider.getGuildsForCommand(SlashCommandData.fromData(command.toData()));
            if (guildIds.isEmpty()) {
                log.debug("No guilds provided for command {}", command.getName());
            }
            guildIds.forEach(id -> {
                guildMapping.putIfAbsent(id, new HashSet<>());
                guildMapping.get(id).add(command);
            });
        }

        guildMapping.forEach((id, commandDataSet) -> {
            Guild guild = jdaContext.getGuildCache().getElementById(id);
            if (guild == null) {
                log.debug("No guilds with id {} found", id);
                return;
            }
            guild.updateCommands().addCommands(commandDataSet).queue();
            log.debug("Done!");
        });
    }

    /**
     * Sends the global scope {@link SlashCommandData} to Discord.
     */
    public void updateGlobalCommands() {
        log.debug("Updating global slash commands...");
        Set<CommandDefinition> globalCommands = commands.stream()
                .filter(it -> it.getCommandScope() == SlashCommand.CommandScope.GLOBAL)
                .collect(Collectors.toSet());
        CommandTree tree = new CommandTree(globalCommands);
        log.debug("Generated command tree:\n" + tree);
        Collection<String> labels = tree.getNames();
        log.debug("Using commands: " + labels);
        jdaContext.performTask(jda -> jda.updateCommands().addCommands(tree.getCommands()).queue());
        log.debug("Done!");
    }


}
