package com.github.kaktushose.jda.commands.reflect;

import com.github.kaktushose.jda.commands.annotations.Command;
import com.github.kaktushose.jda.commands.annotations.CommandController;

/**
 * Class holding information about the command metadata. Mainly used for help embeds and doc generation.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see Command
 * @see CommandController
 * @since 2.0.0
 */
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

    /**
     * Builds a new CommandMetadata.
     *
     * @param command           instance of the corresponding {@link Command} annotation
     * @param commandController instance of the corresponding {@link CommandController} annotation
     * @return CommandMetadata
     */
    public static CommandMetadata build(Command command, CommandController commandController) {
        String category = commandController.category();
        if (!command.category().equals("Other")) {
            category = command.category();
        }
        return new CommandMetadata(command.name(), command.desc(), command.usage(), category);
    }

    /**
     * Gets the command name.
     *
     * @return the command name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the command name.
     *
     * @param name the new name of the command
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the command description.
     *
     * @return the command description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the command description.
     *
     * @param description the new command description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the command usage.
     *
     * @return the command usage
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Sets the command usage.
     *
     * @param usage the new command usage
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * Gets the command category.
     *
     * @return the command category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the command category.
     *
     * @param category the new command category
     */
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
