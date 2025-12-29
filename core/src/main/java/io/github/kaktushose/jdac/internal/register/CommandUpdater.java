package io.github.kaktushose.jdac.internal.register;

import io.github.kaktushose.jdac.annotations.interactions.CommandScope;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.ContextCommandDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.SlashCommandDefinition;
import io.github.kaktushose.jdac.internal.JDAContext;
import io.github.kaktushose.jdac.internal.logging.JDACLogger;
import io.github.kaktushose.jdac.scope.GuildScopeProvider;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// Class that sends the [CommandData] to Discord.
///
/// @implNote Uses a [CommandTree] to properly transpile all [SlashCommandDefinition]s to [SlashCommandData].
/// @see CommandTree
@ApiStatus.Internal
public final class CommandUpdater {

    private static final Logger log = JDACLogger.getLogger(CommandUpdater.class);
    private final JDAContext jdaContext;
    private final GuildScopeProvider guildScopeProvider;
    private final InteractionRegistry interactionRegistry;
    private final LocalizationFunction localizationFunction;

    /// Constructs a new CommandUpdater.
    public CommandUpdater(JDAContext jdaContext,
                          GuildScopeProvider guildScopeProvider,
                          InteractionRegistry registry) {
        this.jdaContext = jdaContext;
        this.guildScopeProvider = guildScopeProvider;
        this.interactionRegistry = registry;
        this.localizationFunction = registry.localizationFunction();
    }

    /// Sends the [SlashCommandData] to Discord. This is equivalent to calling [#updateGlobalCommands()] and
    /// [#updateGuildCommands()] each.
    public void updateAllCommands() {
        updateGuildCommands(null);
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
                        .map(ContextCommandDefinition::toJDAEntity)
                        .toList()
        );
        return commands;
    }

    /// Registers all [CommandDefinition]s with [CommandScope#GLOBAL].
    public void updateGlobalCommands() {
        log.debug("Updating global commands...");
        var commands = getCommands(CommandScope.GLOBAL);
        jdaContext.performTask(jda -> jda.updateCommands().addCommands(commands).queue(), false);
        log.debug("Registered global command(s): {}", commands.stream().map(CommandData::getName).collect(Collectors.toSet()));
    }

    /// Sends the guild scope [SlashCommandData] to Discord.
    public void updateGuildCommands(@Nullable Collection<Guild> guilds) {
        log.debug("Updating guild commands...");
        var guildMapping = getGuildMapping();

        Stream<Guild> update;
        if (guilds == null) {
            update = jdaContext.getGuildCache().stream();
        } else {
            update = guilds.stream();
        }

        update.forEach(guild -> {
            log.debug("Updating guild commands for guild: {}", guild);
            var commands = guildMapping.getOrDefault(guild.getIdLong(), Collections.emptySet());
            guild.updateCommands().addCommands(commands).queue();
            log.debug("Registered guild command(s) {} for {}", commands.stream().map(CommandData::getName).collect(Collectors.toSet()), guild);
        });
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
