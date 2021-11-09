package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;

public class CommandMetadata {

    private String name;
    private String description;
    private String usage;
    private String category;

    private CommandMetadata(String name, String description, String usage, String category) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.category = category;
    }

    public static CommandMetadata build(Command command, CommandController commandController) {
        String category = commandController.category();
        if (!command.category().equals("Other")) {
            category = command.category();
        }
        return new CommandMetadata(command.name(), command.desc(), command.usage(), category);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", usage='" + usage + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
