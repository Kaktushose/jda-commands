package com.github.kaktushose.jda.commands.internal.register;

import com.github.kaktushose.jda.commands.definitions.reflect.interactions.commands.SlashCommandDefinition;
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

/**
 * Single node inside the {@link CommandTree}.
 *
 * @see CommandTree
 * @since 2.3.0
 */
@ApiStatus.Internal
public record TreeNode(
        String name,
        SlashCommandDefinition command,
        List<TreeNode> children
) implements Iterable<TreeNode> {

    private static final Logger log = LoggerFactory.getLogger(SlashCommandUpdater.class);

    /**
     * Constructs an empty TreeNode. Should only be used for root nodes.
     */
    public TreeNode() {
        this("", null);
    }

    /**
     * Constructs a new TreeNode.
     *
     * @param name    the name of the command
     * @param command the {@link SlashCommandDefinition}
     */
    public TreeNode(@NotNull String name, @Nullable SlashCommandDefinition command) {
        this(name, command, new ArrayList<>());
    }

    /**
     * Adds a child {@link TreeNode} either as a child of this {@link TreeNode} or to one of its children based on the
     * amount of labels.
     *
     * <p>For instance {@code labels[0]} will be added as a child {@link TreeNode} to this
     * {@link TreeNode}, {@code labels[1]} will be added as a child to the child {@link TreeNode} created from
     * {@code labels[0]} and so on.
     *
     * <p>This guarantees to create a {@link CommandTree} that respects Subcommands and SubcommandGroups.
     *
     * @param labels  an Array of all labels, can be empty
     * @param command the {@link SlashCommandDefinition} to add
     */
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

    /**
     * Gets a child {@link TreeNode} based on its name.
     *
     * @param name the label to get the child {@link TreeNode} from
     * @return an {@link Optional} holding the result
     */
    public Optional<TreeNode> getChild(String name) {
        return children.stream().filter(child -> child.name.equals(name)).findFirst();
    }

    /**
     * Gets the label of the {@link SlashCommandDefinition} of this {@link TreeNode}.
     *
     * @return the label of the {@link SlashCommandDefinition}
     */
    public String getName() {
        return name;
    }

    /**
     * Gets all children {@link TreeNode TreeNodes}.
     *
     * @return all children {@link TreeNode TreeNodes}
     */
    public List<TreeNode> getChildren() {
        return children;
    }

    /**
     * Gets whether this {@link TreeNode} has children.
     *
     * @return {@code true} if this {@link TreeNode} has children
     */
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    /**
     * Gets the {@link SlashCommandDefinition} of this {@link TreeNode}. Returns an empty {@link Optional} if one or more
     * children exist or if the {@link SlashCommandDefinition} is {@code null}.
     *
     * @return an {@link Optional} holding the result
     */
    public Optional<SlashCommandDefinition> getCommand() {
        if (!children.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(command);
    }

    /**
     * Gets all names of the leaf nodes.
     *
     * @return a {@link List} of all names of the leaf nodes.
     */
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

    /**
     * Gets all {@link SlashCommandData of the leaf nodes}.
     *
     * @return a {@link List} of all {@link SlashCommandData of the leaf nodes.
     */
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
            commands.add(command.toCommandData());
        } catch (Exception e) {
            log.error(String.format("Cannot convert command %s.%s to  SlashCommandData!",
                    command.getMethod().getDeclaringClass().getSimpleName(),
                    command.getMethod().getName()), e
            );
        }
    }

    private SlashCommandData createRootCommand(String name, List<TreeNode> children) {
        SlashCommandData result = Commands.slash(name, "empty description");
        List<SlashCommandDefinition> subCommands = unwrapDefinitions(children);
        LocalizationFunction function = subCommands.getFirst().getLocalizationFunction();

        boolean isNSFW = false;
        boolean isGuildOnly = false;
        Set<Permission> enabledPermissions = new HashSet<>();
        for (SlashCommandDefinition command : subCommands) {
            isNSFW = isNSFW || command.isNSFW();
            isGuildOnly = isGuildOnly || command.isGuildOnly();
            enabledPermissions.addAll(command.getEnabledPermissions());
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
