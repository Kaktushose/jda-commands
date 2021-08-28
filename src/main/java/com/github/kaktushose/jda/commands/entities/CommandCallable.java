package com.github.kaktushose.jda.commands.entities;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * This class represents an executable command and all its attributes.
 *
 * @author Kaktushose
 * @version 1.0.0
 * @see com.github.kaktushose.jda.commands.rewrite.annotations.Command
 * @since 1.0.0
 */
public class CommandCallable {

    private final List<String> labels;
    private final String name;
    private final String description;
    private final String usage;
    private final String category;
    private final List<Parameter> parameters;
    private final Set<String> permissions;
    private final Method method;
    private final Object instance;

    /**
     * Constructs a CommandCallable.
     *
     * @param labels      a list of all labels for the command
     * @param name        the name of the command
     * @param description the description of the command
     * @param usage       a short example on how to use the command
     * @param category    the category of the command
     * @param parameters  a list of {@link Parameter}s the command needs
     * @param permissions a set of all permissions the command requires
     * @param method      the command method
     * @param instance    an instance of the class where the command method is declared
     */
    public CommandCallable(@Nonnull List<String> labels,
                           @Nonnull String name,
                           @Nonnull String description,
                           @Nonnull String usage,
                           @Nonnull String category,
                           @Nonnull List<Parameter> parameters,
                           @Nonnull Set<String> permissions,
                           @Nonnull Method method,
                           @Nonnull Object instance) {
        this.labels = labels;
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.category = category;
        this.parameters = parameters;
        this.permissions = permissions;
        this.method = method;
        this.instance = instance;
    }

    /**
     * Get a list of all labels for the command.
     *
     * @return a list of all labels
     */
    public List<String> getLabels() {
        return labels;
    }

    /**
     * Get the name of the command.
     *
     * @return the name of the command
     */
    public String getName() {
        return name;
    }

    /**
     * Get the description of the command.
     *
     * @return the command description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get a short example on how to use the command.
     *
     * @return example usage of the command
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Get the category of the command.
     *
     * @return the category of the command
     */
    public String getCategory() {
        return category;
    }

    /**
     * Get a list of all {@link Parameter}s this command needs to be executed.
     * The first parameter is always of type {@link CommandEvent}
     *
     * @return a List list of all {@link Parameter}s
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * Get a list of all permissions this command requires to be executed.
     *
     * @return a Set of all permissions this command requires
     */
    public Set<String> getPermissions() {
        return permissions;
    }

    /**
     * Get the method that can be invoked to execute the command.
     *
     * @return the method of the command
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Get an instance of the controller class of the command.
     *
     * @return an instance of the class where the command is defined
     */
    public Object getControllerInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return method.getName() + "{" +
                "labels=" + labels +
                ", parameters=" + parameters +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", usage='" + usage + '\'' +
                ", category='" + category + '\'' +
                ", instance=" + instance +
                '}';
    }
}
