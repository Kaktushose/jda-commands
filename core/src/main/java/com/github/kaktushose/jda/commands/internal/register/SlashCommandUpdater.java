package com.github.kaktushose.jda.commands.internal.register;

import com.github.kaktushose.jda.commands.JDAContext;
import com.github.kaktushose.jda.commands.annotations.interactions.CommandScope;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionRegistry;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.ContextCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.i18n.Localizer;
import com.github.kaktushose.jda.commands.i18n.internal.JDACLocalizationFunction;
import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/// Class that sends the [CommandData] to Discord.
///
/// @implNote Uses a [CommandTree] to properly transpile all [SlashCommandDefinition]s to [SlashCommandData].
/// @see CommandTree
@ApiStatus.Internal
public final class SlashCommandUpdater {

    private static final Logger log = LoggerFactory.getLogger(SlashCommandUpdater.class);
    private final JDAContext jdaContext;
    private final GuildScopeProvider guildScopeProvider;
    private final InteractionRegistry interactionRegistry;
    private final LocalizationFunction localizationFunction;

    /// Constructs a new SlashCommandUpdater.
    public SlashCommandUpdater(JDAContext jdaContext,
                               GuildScopeProvider guildScopeProvider,
                               InteractionRegistry registry,
                               Localizer localizer) {
        this.jdaContext = jdaContext;
        this.guildScopeProvider = guildScopeProvider;
        this.interactionRegistry = registry;
        this.localizationFunction = new JDACLocalizationFunction(localizer);
    }

    /// Sends the [SlashCommandData] to Discord. This is equivalent to calling [#updateGlobalCommands()] and
    /// [#updateGuildCommands()] each.
    public void updateAllCommands() {
        updateGuildCommands();
        updateGlobalCommands();
    }

    private Set<CommandData> getCommands(CommandScope scope) {
        var tree = new CommandTree(
                interactionRegistry.find(SlashCommandDefinition.class, it -> it.commandConfig().scope() == scope)
        );
        Set<CommandData> commands = new HashSet<>(tree.getSlashCommandData(localizationFunction));
        log.debug("Generated slash command tree with CommandScope.{}:\n{}", scope, tree);
        commands.addAll(
                interactionRegistry.find(ContextCommandDefinition.class, it -> it.commandConfig().scope() == scope)
                        .stream()
                        .map(definition -> definition.toJDAEntity())
                        .toList()
        );
        return commands;
    }

    /// Registers all [CommandDefinition]s with [CommandScope#GLOBAL].
    public void updateGlobalCommands() {
        log.debug("Updating global commands...");
        var commands = getCommands(CommandScope.GLOBAL);
        jdaContext.performTask(jda -> jda.updateCommands().addCommands(commands).queue());
        log.debug("Registered global command(s): {}", commands.stream().map(CommandData::getName).collect(Collectors.toSet()));
    }

    /// Sends the guild scope [SlashCommandData] to Discord.
    public void updateGuildCommands() {
        log.debug("Updating guild commands...");
        var guildMapping = getGuildMapping();
        for (var guild : jdaContext.getGuildCache()) {
            var commands = guildMapping.getOrDefault(guild.getIdLong(), Collections.emptySet());
            guild.updateCommands().addCommands(commands).queue();
            log.debug("Registered guild command(s) {} for {}", commands.stream().map(CommandData::getName).collect(Collectors.toSet()), guild);
        }
    }

    private Map<Long, Set<CommandData>> getGuildMapping() {
        var commands = getCommands(CommandScope.GUILD);
        log.debug("Interactions eligible for registration: {}", commands.stream().map(CommandData::getName).collect(Collectors.toSet()));

        Map<Long, Set<CommandData>> guildMapping = new HashMap<>();
        for (var command : commands) {
            // create a copy so that a user doesn't modify the command data used for registration
            var guildIds = guildScopeProvider.apply(CommandData.fromData(command.toData()));
            if (guildIds.isEmpty()) {
                log.debug("No guilds provided for command \"{}\"", command.getName());
            } else {
                log.debug("Using guild(s) {} for command \"{}\"", guildIds, command.getName());
            }
            guildIds.forEach(id -> {
                guildMapping.putIfAbsent(id, new HashSet<>());
                guildMapping.get(id).add(command);
            });
        }
        return guildMapping;
    }
}
