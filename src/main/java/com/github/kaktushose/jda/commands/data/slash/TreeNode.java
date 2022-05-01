package com.github.kaktushose.jda.commands.data.slash;

import com.github.kaktushose.jda.commands.reflect.CommandDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TreeNode implements Iterable<TreeNode> {

    private final String label;
    private final CommandDefinition command;
    private final List<TreeNode> children;

    public TreeNode() {
        this("", null);
    }

    public TreeNode(String label, CommandDefinition command) {
        this.label = label;
        this.command = command;
        children = new ArrayList<>();
    }

    public void addChild(String[] labels, CommandDefinition command) {
        if (labels.length < 1) {
            return;
        }
        String rootLabel = labels[0];
        String[] childrenLabels = new String[0];
        if (labels.length > 1) {
            childrenLabels = Arrays.copyOfRange(labels, 1, labels.length);
        }
        Optional<TreeNode> optional = getChildren(rootLabel);
        if (optional.isPresent()) {
            optional.get().addChild(childrenLabels, command);
        } else {
            TreeNode child = new TreeNode(rootLabel, command);
            children.add(child);
            child.addChild(childrenLabels, command);
        }
    }

    public Optional<TreeNode> getChildren(String label) {
        return children.stream().filter(child -> child.label.equals(label)).findFirst();
    }

    public String getLabel() {
        return label;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public Optional<CommandDefinition> getCommand() {
        if (children.size() > 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(command);
    }

    public List<CommandDefinition> getCommands(TreeNode node) {
        List<CommandDefinition> commands = new ArrayList<>();
        for (TreeNode child : node.getChildren()) {
            commands.addAll(getCommands(child));
        }
        node.getCommand().ifPresent(commands::add);
        return commands;
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
