package com.github.kaktushose.jda.commands.data.slash;

import com.github.kaktushose.jda.commands.interactions.commands.SlashCommandUpdater;
import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Single node inside the {@link CommandTree}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see CommandTree
 * @since 2.3.0
 */
public class TreeNode implements Iterable<TreeNode> {

    private static final Logger log = LoggerFactory.getLogger(SlashCommandUpdater.class);
    private final String name;
    private final CommandDefinition command;
    private final List<TreeNode> children;

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
     * @param command the {@link CommandDefinition}
     */
    public TreeNode(@NotNull String name, @Nullable CommandDefinition command) {
        this.name = name;
        this.command = command;
        children = new ArrayList<>();
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
     * @param command the {@link CommandDefinition} to add
     */
    public void addChild(@NotNull String[] labels, @NotNull CommandDefinition command) {
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
     * Gets the label of the {@link CommandDefinition} of this {@link TreeNode}.
     *
     * @return the label of the {@link CommandDefinition}
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
     * Gets the {@link CommandDefinition} of this {@link TreeNode}. Returns an empty {@link Optional} if one or more
     * children exist or if the {@link CommandDefinition} is {@code null}.
     *
     * @return an {@link Optional} holding the result
     */
    public Optional<CommandDefinition> getCommand() {
        if (children.size() > 0) {
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
            SlashCommandData data = Commands.slash(name, "empty description");
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
