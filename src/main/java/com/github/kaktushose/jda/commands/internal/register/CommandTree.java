package com.github.kaktushose.jda.commands.internal.register;

import com.github.kaktushose.jda.commands.definitions.interactions.impl.command.SlashCommandDefinition;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.List;

/// A tree data structure representing slash commands sorted into Subcommands and SubcommandGroups. Each [TreeNode]
/// can have _n_ children, however the maximum level is _3_ due to Discords limitations on SubcommandGroups.
///
/// @see TreeNode
/// @see <a href="https://discord.com/developers/docs/interactions/application-commands#subcommands-and-subcommand-groups">Discord Subcommands and Subcommand Groups Documentation</a>
@ApiStatus.Internal
public record CommandTree(
        TreeNode root
) {

    /// Constructs an empty CommandTree.
    public CommandTree() {
        this(new TreeNode());
    }

    /// Constructs a new CommandTree.
    public CommandTree(Collection<SlashCommandDefinition> commands) {
        this(new TreeNode());
        addAll(commands);
    }

    /// Adds a [SlashCommandDefinition] to the [CommandTree]. The label of the [SlashCommandDefinition] will be
    /// sanitized to match the regex `^[\w-]+$`. Furthermore, if the label consists of more than three spaces any
    /// additional space will be replaced with `_` due to Discords limitations on SubcommandGroups.
    ///
    /// @param command the [SlashCommandDefinition] to add
    public void add(SlashCommandDefinition command) {
        root.addChild(resolveLabel(command.name()), command);
    }

    /// Adds all [SlashCommandDefinition]s of the [Collection] to the [CommandTree].
    ///
    /// @param commands a [Collection] of [SlashCommandDefinition]s to add
    /// @see #add(SlashCommandDefinition)
    public void addAll(Collection<SlashCommandDefinition> commands) {
        commands.forEach(this::add);
    }

    private String[] resolveLabel(String label) {
        String[] split = label.split(" ", 3);
        if (split.length > 3) {
            split[4] = split[4].replaceAll(" ", "_");
        }
        for (int i = 0; i < split.length; i++) {
            for (char c : split[i].toCharArray()) {
                if (String.valueOf(c).equals(" ")) {
                    split[i] = split[i].replace(String.valueOf(c), "_");
                }
                if (!String.valueOf(c).matches("^[\\w-]+$")) {
                    split[i] = split[i].replace(String.valueOf(c), "");
                }
                split[i] = split[i].toLowerCase();
            }
        }
        return split;
    }

    /// Gets all [SlashCommandData].This will only return the [SlashCommandData] of the leaf nodes.
    ///
    /// @return a [List] of [SlashCommandData]
    public List<SlashCommandData> getCommands() {
        return root.getCommandData();
    }

    /// Gets the sanitized labels of all [SlashCommandData] returned by [#getCommands()].
    /// The labels will match the regex `^[\w-]+$`. Furthermore, if the label consists of more than three spaces
    /// any additional space will be replaced with `_` due to Discords limitations on SubcommandGroups.
    ///
    /// @return a [List] of labels
    public List<String> getNames() {
        return root.getNames();
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
