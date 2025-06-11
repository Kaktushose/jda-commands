package com.github.kaktushose.jda.commands.internal.register;

import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;

import java.util.ArrayList;
import java.util.Collection;

/// A tree data structure representing slash commands sorted into Subcommands and SubcommandGroups. Each [TreeNode]
/// can have _n_ children, however the maximum depth is _3_ due to Discords limitations on SubcommandGroups.
///
/// @see TreeNode
/// @see <a href="https://discord.com/developers/docs/interactions/application-commands#subcommands-and-subcommand-groups">Discord Subcommands and Subcommand Groups Documentation</a>
public record CommandTree(TreeNode root) {

    /// Constructs a new CommandTree from the given [SlashCommandDefinition]s.
    ///
    /// The labels of the [SlashCommandDefinition]s will be sanitized to match the regex `^[\w-]+$`. Furthermore, if the
    /// label consists of more than three spaces any additional space will be replaced with `_` due to Discords
    /// limitations on SubcommandGroups.
    public CommandTree(Collection<SlashCommandDefinition> commands) {
        this(new TreeNode("", null, new ArrayList<>()));
        commands.forEach(command -> root.addChild(resolveLabel(command.name()), command));
    }

    /// Takes an arbitrary String as input and sanitizes it to conform to Discords limitations
    private String[] resolveLabel(String label) {
        String[] split = label.split(" ", 3);
        for (int i = 0; i < split.length; i++) {
            for (char c : split[i].toCharArray()) {
                if (!String.valueOf(c).matches("^[\\w-]+$")) {
                    split[i] = split[i].replace(String.valueOf(c), "");
                }
                split[i] = split[i].toLowerCase();
            }
        }
        return split;
    }

    /// Transforms this CommandTree to [SlashCommandData] based on the tree structure.
    ///
    /// More formally, a depth-first-search is performed to determine which commands should be registered as a slash
    /// command, a sub command group or a sub command.
    ///
    /// @return a [Collection] of [SlashCommandData]
    public Collection<SlashCommandData> getSlashCommandData(LocalizationFunction localizationFunction) {
        return root.children().stream().map(node -> node.toSlashCommandData(localizationFunction)).toList();
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
