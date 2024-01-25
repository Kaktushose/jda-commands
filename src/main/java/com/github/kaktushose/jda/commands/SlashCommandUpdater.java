package com.github.kaktushose.jda.commands;

import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.data.CommandTree;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.GenericCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that sends the {@link SlashCommandData} to Discord. Uses a {@link CommandTree} to properly transpile all
 * {@link SlashCommandDefinition CommandDefinitions} to {@link SlashCommandData}.
 *
 * @see CommandTree
 * @since 2.3.0
 */
public class SlashCommandUpdater {

    private static final Logger log = LoggerFactory.getLogger(SlashCommandUpdater.class);
    private final JDAContext jdaContext;
    private final GuildScopeProvider guildScopeProvider;
    private final InteractionRegistry interactionRegistry;

    /**
     * Constructs a new SlashCommandUpdater.
     *
     * @param jdaCommands the corresponding {@link JDACommands} instance
     */
    public SlashCommandUpdater(JDACommands jdaCommands) {
        this.jdaContext = jdaCommands.getJDAContext();
        guildScopeProvider = jdaCommands.getImplementationRegistry().getGuildScopeProvider();
        interactionRegistry = jdaCommands.getInteractionRegistry();
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
        log.debug("Updating guild commands...");

        Set<GenericCommandDefinition> guildCommands = interactionRegistry.getCommands()
                .stream()
                .filter(it -> it.getCommandScope() == SlashCommand.CommandScope.GUILD)
                .collect(Collectors.toSet());

        CommandTree tree = new CommandTree(
                guildCommands.stream()
                        .filter(it -> it.getCommandType() == Command.Type.SLASH)
                        .map(it -> (SlashCommandDefinition) it)
                        .collect(Collectors.toSet())
        );
        log.debug("Generated slash command tree:\n" + tree);

        Set<CommandData> result = new HashSet<>();
        result.addAll(tree.getCommands());
        result.addAll(guildCommands.stream().
                filter(it -> (it.getCommandType() == Command.Type.USER || it.getCommandType() == Command.Type.MESSAGE))
                .map(GenericCommandDefinition::toCommandData)
                .collect(Collectors.toSet())
        );
        log.debug("Registering commands: " + result.stream().map(CommandData::getName).collect(Collectors.toSet()));

        Map<Long, Set<CommandData>> guildMapping = new HashMap<>();
        for (CommandData command : result) {
            // create a copy so that a user doesn't modify the command data used for registration
            Set<Long> guildIds = guildScopeProvider.getGuildsForCommand(CommandData.fromData(command.toData()));
            if (guildIds.isEmpty()) {
                log.debug("No guilds provided for command {}", command.getName());
            } else {
                log.debug("Using guilds {} for command {}", guildIds, command.getName());
            }
            guildIds.forEach(id -> {
                guildMapping.putIfAbsent(id, new HashSet<>());
                guildMapping.get(id).add(command);
            });
        }

        for (Guild guild : jdaContext.getGuildCache()) {
            Set<CommandData> commands = guildMapping.getOrDefault(guild.getIdLong(), Collections.emptySet());
            guild.updateCommands().addCommands(commands).queue();
            log.debug("Done!");
        }
    }

    /**
     * Sends the global scope {@link SlashCommandData} to Discord.
     */
    public void updateGlobalCommands() {
        log.debug("Updating global commands...");

        Set<GenericCommandDefinition> globalCommands = interactionRegistry.getCommands()
                .stream()
                .filter(it -> it.getCommandScope() == SlashCommand.CommandScope.GLOBAL)
                .collect(Collectors.toSet());

        CommandTree tree = new CommandTree(
                globalCommands.stream()
                        .filter(it -> it.getCommandType() == Command.Type.SLASH)
                        .map(it -> (SlashCommandDefinition) it)
                        .collect(Collectors.toSet())
        );
        log.debug("Generated slash command tree:\n" + tree);

        Set<CommandData> result = new HashSet<>();
        result.addAll(tree.getCommands());
        result.addAll(globalCommands.stream().
                filter(it -> (it.getCommandType() == Command.Type.USER || it.getCommandType() == Command.Type.MESSAGE))
                .map(GenericCommandDefinition::toCommandData)
                .collect(Collectors.toSet())
        );
        log.debug("Registering commands: " + result.stream().map(CommandData::getName).collect(Collectors.toSet()));

        jdaContext.performTask(jda -> jda.updateCommands().addCommands(result).queue());
        log.debug("Done!");
    }
}
