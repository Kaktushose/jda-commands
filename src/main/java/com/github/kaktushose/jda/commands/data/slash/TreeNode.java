package com.github.kaktushose.jda.commands.data.slash;

import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Single node inside the {@link CommandTree}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @since 2.3.0
 */
public class TreeNode implements Iterable<TreeNode> {

    private final String label;
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
     * @param label   the label of the command
     * @param command the {@link CommandDefinition}
     */
    public TreeNode(@NotNull String label, @Nullable CommandDefinition command) {
        this.label = label;
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
     * Gets a child {@link TreeNode} based on its label.
     *
     * @param label the label to get the child {@link TreeNode} from
     * @return an {@link Optional} holding the result
     */
    public Optional<TreeNode> getChild(String label) {
        return children.stream().filter(child -> child.label.equals(label)).findFirst();
    }

    /**
     * Gets the label of the {@link CommandDefinition} of this {@link TreeNode}.
     *
     * @return the label of the {@link CommandDefinition}
     */
    public String getLabel() {
        return label;
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
     * Gets the {@link CommandDefinition} of this {@link TreeNode}. Returns an empty {@link Optional} if one or more
     * children exist.
     *
     * @return an {@link Optional} holding the result
     */
    public Optional<CommandDefinition> getCommand() {
        if (children.size() > 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(command);
    }

    @NotNull
    @Override
    public Iterator<TreeNode> iterator() {
        return children.iterator();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(50);
        print(builder, "", "");
        return builder.toString();
    }

    private void print(StringBuilder builder, String prefix, String childrenPrefix) {
        builder.append(prefix);
        builder.append(label);
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
