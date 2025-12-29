package io.github.kaktushose.jdac.internal.register;

import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.SlashCommandDefinition;
import io.github.kaktushose.jdac.exceptions.ConfigurationException;
import io.github.kaktushose.jdac.exceptions.InternalException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

/// Single node inside the [CommandTree].
///
/// @see CommandTree
@ApiStatus.Internal
public record TreeNode(String name, SlashCommandDefinition command, List<TreeNode> children) implements Iterable<TreeNode> {

    /// Constructs a new TreeNode.
    ///
    /// @param name    the name of the command
    /// @param command the [SlashCommandDefinition]
    public TreeNode(String name, SlashCommandDefinition command) {
        this(name, command, new ArrayList<>());
    }

    /// Attempts to add a child [TreeNode] either as a child of this [TreeNode] or to one of its children based on the
    /// given labels.
    ///
    /// @param labels  an Array of all labels, can be empty
    /// @param command the [SlashCommandDefinition] to add
    /// @implNote Traverses the tree based on the given labels. If more than one label is passed, creates a group note
    /// with label[0] as the name and passes the remaining label[1,2] to that note. This process is repeated until only
    /// one label is passed which will be added as a leaf node.
    ///
    /// This guarantees to create a [CommandTree] that respects Subcommands and SubcommandGroups.
    public void addChild(String[] labels, SlashCommandDefinition command) {
        if (labels.length == 0) {
            throw new InternalException("wrong-labels", entry("command", command.displayName()), entry("labelCount", 0));
        }

        String rootLabel = labels[0];
        // only one label left -> we've reached the end leaf aka traversed the tree to the maximum depth
        if (labels.length == 1) {
            if (checkDuplicate(rootLabel, command)) {
                return;
            }
            children.add(new TreeNode(rootLabel, command));
            return;
        }
        // framework error, SlashCommandDefinition should have prevented this
        if (labels.length > 3) {
            throw new InternalException("wrong-labels", entry("command", command.displayName()), entry("labelCount", 0));
        }
        // get or create node for current label
        TreeNode child = getChild(rootLabel).orElseGet(() -> {
            TreeNode node = new TreeNode(rootLabel, null); // in between nodes don't get a command definition assigned
            children.add(node);
            return node;
        });
        // pass on remaining labels
        child.addChild(Arrays.copyOfRange(labels, 1, labels.length), command);
    }

    private boolean checkDuplicate(String label, CommandDefinition command) {
        var child = getChild(label);
        if (child.isEmpty()) {
            return false;
        }
        var duplicate = child.get().command;
        children.remove(child.get());
        throw new ConfigurationException(
                "duplicate-commands",
                entry("display", command.displayName()),
                entry("command", "%s.%s".formatted(command.classDescription().name(), command.methodDescription().name())),
                entry("command", "%s.%s".formatted(duplicate.classDescription().name(), duplicate.methodDescription().name()))
        );
    }

    /// Gets a child [TreeNode] based on its name.
    ///
    /// @param name the label to get the child [TreeNode] from
    /// @return an [Optional] holding the result
    public Optional<TreeNode> getChild(String name) {
        return children.stream().filter(child -> child.name.equals(name)).findFirst();
    }

    /// Transforms this [TreeNode] into [SlashCommandData].
    ///
    /// The children of this [TreeNode] will be added to the [SlashCommandData] either as [SubcommandGroupData] or
    /// [SubcommandData] depending on whether the children of this [TreeNode] also have children.
    public SlashCommandData toSlashCommandData(LocalizationFunction localization) {
        if (!children.isEmpty()) {
            SlashCommandData root = Commands.slash(name, "empty description").setLocalizationFunction(localization);
            children.forEach(child -> child.addSubcommandGroupData(root));
            return root;
        }

        return (command.toJDAEntity());
    }

    /// Transforms this TreeNode into [SubcommandGroupData] if it has children, else transforms it directly to [SubcommandData].
    /// If this TreeNode has children, they will be added as [SubcommandData] to the [SubcommandGroupData].
    ///
    /// The [SubcommandGroupData] (or [SubcommandData]) will be added to the passed root [SlashCommandData].
    private void addSubcommandGroupData(SlashCommandData root) {
        if (!children.isEmpty()) {
            SubcommandGroupData group = new SubcommandGroupData(name, "empty description");
            children.forEach(child -> {
                combine(root, child.command());
                child.addSubcommandData(group);
            });
            root.addSubcommandGroups(group);
        } else {
            combine(root, command);
            root.addSubcommands(command.toSubcommandData(name));
        }
    }

    /// Transforms this TreeNode into [SubcommandData] and adds it to the passed [SubcommandGroupData].
    private void addSubcommandData(SubcommandGroupData group) {
        if (!children.isEmpty()) {
            throw new InternalException("subcommand-with-children");
        }
        group.addSubcommands(command.toSubcommandData(name));
    }

    /// Combines the settings of the given [SlashCommandData] and the given [SlashCommandDefinition].
    ///
    /// @param root       the [SlashCommandData] that is the root command for the given [SlashCommandDefinition]
    /// @param definition a [SlashCommandDefinition] that will be added as a subcommand to the given root [SlashCommandData]
    private void combine(SlashCommandData root, SlashCommandDefinition definition) {
        Long permissionsRaw = root.getDefaultPermissions().getPermissionsRaw();
        Set<Permission> permissions = permissionsRaw == null
                ? EnumSet.noneOf(Permission.class)
                : Permission.getPermissions(permissionsRaw);
        permissions.addAll(Arrays.asList(definition.commandConfig().enabledPermissions()));

        Set<InteractionContextType> context = new HashSet<>(root.getContexts());
        context.addAll(Arrays.asList(definition.commandConfig().context()));

        Set<IntegrationType> integration = new HashSet<>(root.getIntegrationTypes());
        integration.addAll(Arrays.asList(definition.commandConfig().integration()));

        root.setNSFW(root.isNSFW() | definition.commandConfig().isNSFW())
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(permissions))
                .setContexts(context)
                .setIntegrationTypes(integration);
    }

    @Override
    public Iterator<TreeNode> iterator() {
        return children.iterator();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(50);
        buildStringTree(builder, "", "");
        return builder.toString();
    }

    private void buildStringTree(StringBuilder builder, String prefix, String childrenPrefix) {
        builder.append(prefix);
        builder.append(name);
        builder.append('\n');
        Iterator<TreeNode> it = children.iterator();
        while (it.hasNext()) {
            TreeNode next = it.next();
            if (it.hasNext()) {
                next.buildStringTree(builder, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.buildStringTree(builder, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }
}
