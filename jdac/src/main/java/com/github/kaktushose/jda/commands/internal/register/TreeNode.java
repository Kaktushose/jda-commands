package com.github.kaktushose.jda.commands.internal.register;

import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/// Single node inside the [CommandTree].
///
/// @see CommandTree
@ApiStatus.Internal
public record TreeNode(
        String name,
        SlashCommandDefinition command,
        List<TreeNode> children
) implements Iterable<TreeNode> {

    private static final Logger log = LoggerFactory.getLogger(TreeNode.class);

    /// Constructs an empty TreeNode. Should only be used for root nodes.
    public TreeNode() {
        this("", null);
    }

    /// Constructs a new TreeNode.
    ///
    /// @param name    the name of the command
    /// @param command the [SlashCommandDefinition]
    public TreeNode(@NotNull String name, @Nullable SlashCommandDefinition command) {
        this(name, command, new ArrayList<>());
    }

    /// Adds a child [TreeNode] either as a child of this [TreeNode] or to one of its children based on the
    /// amount of labels.
    ///
    /// For instance `labels[0]` will be added as a child [TreeNode] to this
    /// [TreeNode], `labels[1]` will be added as a child to the child [TreeNode] created from
    /// `labels[0]` and so on.
    ///
    /// This guarantees to create a [CommandTree] that respects Subcommands and SubcommandGroups.
    ///
    /// @param labels  an Array of all labels, can be empty
    /// @param command the [SlashCommandDefinition] to add
    public void addChild(@NotNull String[] labels, @NotNull SlashCommandDefinition command) {
        if (labels.length < 1) {
            return;
        }
        String rootLabel = labels[0];
        String[] childrenLabels = new String[0];
        if (labels.length > 1) {
            childrenLabels = Arrays.copyOfRange(labels, 1, labels.length);
        }
        Optional<TreeNode> optional = getChild(rootLabel);
        if (optional.isPresent()) {
            optional.get().addChild(childrenLabels, command);
        } else {
            TreeNode child = new TreeNode(rootLabel, command);
            children.add(child);
            child.addChild(childrenLabels, command);
        }
    }

    /// Gets a child [TreeNode] based on its name.
    ///
    /// @param name the label to get the child [TreeNode] from
    /// @return an [Optional] holding the result
    public Optional<TreeNode> getChild(String name) {
        return children.stream().filter(child -> child.name.equals(name)).findFirst();
    }

    /// Gets the name of the [SlashCommandDefinition] of this [TreeNode].
    ///
    /// @return the name of the [SlashCommandDefinition]
    public String getName() {
        return name;
    }

    /// Gets all children [TreeNode]s.
    ///
    /// @return all children [TreeNode]s
    public List<TreeNode> getChildren() {
        return children;
    }

    /// Gets whether this [TreeNode] has children.
    ///
    /// @return `true` if this [TreeNode] has children
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    /// Gets the [SlashCommandDefinition] of this [TreeNode]. Returns an empty [Optional] if one or more
    /// children exist or if the [SlashCommandDefinition] is `null`.
    ///
    /// @return an [Optional] holding the result
    public Optional<SlashCommandDefinition> getCommand() {
        if (!children.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(command);
    }

    /// Gets all names of the leaf nodes.
    ///
    /// @return a [List] of all names of the leaf nodes.
    public List<String> getNames() {
        List<String> result = new ArrayList<>();
        toLabel(result, "");
        return result;
    }

    private void toLabel(List<String> labels, String root) {
        if (hasChildren()) {
            children.forEach(child -> child.toLabel(labels, (root + " " + name).trim()));
        } else {
            labels.add((root + " " + name).trim());
        }
    }

    /// Gets all [of the leaf nodes][SlashCommandData].
    ///
    /// @return a [List] of [SlashCommandData]
    public List<SlashCommandData> getCommandData() {
        List<SlashCommandData> result = new ArrayList<>();
        children.forEach(child -> child.toCommandData(result));
        return result;
    }

    private void toCommandData(Collection<SlashCommandData> commands) {
        if (command == null) {
            return;
        }
        if (hasChildren()) {
            SlashCommandData data = createRootCommand(name, children);
            children.forEach(child -> child.toSubCommandData(data));
            commands.add(data);
            return;
        }
        try {
            commands.add(command.toJDAEntity());
        } catch (Exception e) {
            log.error("Cannot convert command {}.{} to  SlashCommandData!", command.classDescription().name(), command.methodDescription().name(), e);
        }
    }

    private SlashCommandData createRootCommand(String name, List<TreeNode> children) {
        SlashCommandData result = Commands.slash(name, "empty description");
        List<SlashCommandDefinition> subCommands = unwrapDefinitions(children);
        LocalizationFunction function = subCommands.getFirst().localizationFunction();

        boolean isNSFW = false;
        boolean isGuildOnly = false;
        Set<Permission> enabledPermissions = new HashSet<>();
        for (SlashCommandDefinition command : subCommands) {
            isNSFW = isNSFW || command.nsfw();
            isGuildOnly = isGuildOnly || command.guildOnly();
            enabledPermissions.addAll(command.enabledPermissions());
        }

        return result.setDefaultPermissions(DefaultMemberPermissions.enabledFor(enabledPermissions))
                .setNSFW(isNSFW)
                .setGuildOnly(isGuildOnly)
                .setLocalizationFunction(function);
    }

    private List<SlashCommandDefinition> unwrapDefinitions(List<TreeNode> children) {
        List<SlashCommandDefinition> result = new ArrayList<>();
        for (TreeNode child : children) {
            if (child.getCommand().isPresent()) {
                result.add(child.getCommand().get());
            } else {
                result.addAll(unwrapDefinitions(child.getChildren()));
            }
        }
        return result;
    }

    private void toSubCommandData(SlashCommandData commandData) {
        if (command == null) {
            return;
        }
        if (hasChildren()) {
            SubcommandGroupData data = new SubcommandGroupData(name, "empty description");
            children.forEach(child -> child.getCommand().ifPresent(command -> data.addSubcommands(command.toSubCommandData(child.name))));
            commandData.addSubcommandGroups(data);
        } else {
            commandData.addSubcommands(command.toSubCommandData(name));
        }
    }

    @NotNull
    @Override
    public Iterator<TreeNode> iterator() {
        return children.iterator();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(50);
        print(builder, "", "");
        return builder.toString();
    }

    private void print(StringBuilder builder, String prefix, String childrenPrefix) {
        builder.append(prefix);
        builder.append(name);
        builder.append('\n');
        Iterator<TreeNode> it = children.iterator();
        while (it.hasNext()) {
            TreeNode next = it.next();
            if (it.hasNext()) {
                next.print(builder, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.print(builder, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }
}
