package com.github.kaktushose.jda.commands.data.slash;

import com.github.kaktushose.jda.commands.reflect.CommandDefinition;

import java.util.List;

public class CommandTree {

    private final TreeNode root;

    public CommandTree() {
        root = new TreeNode();
    }

    public void add(CommandDefinition command) {
        root.addChild(resolveLabel(command.getLabels().get(0)), command);
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
            }
        }
        return split;
    }

    public List<CommandDefinition> getCommands() {
        return root.getCommands(root);
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
