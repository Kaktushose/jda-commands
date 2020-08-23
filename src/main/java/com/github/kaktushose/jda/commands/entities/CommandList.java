package com.github.kaktushose.jda.commands.entities;

import com.github.kaktushose.jda.commands.annotations.Command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is a subclass of {@link ArrayList}. This barely differs from a normal {@link ArrayList} and just provides
 * some methods for easier sorting commands.
 *
 * @author Kaktushose
 * @version 1.0.0
 * @since 1.0.0
 */
public class CommandList extends ArrayList<CommandCallable> {

    /**
     * Constructs an empty list.
     */
    public CommandList() {

    }

    /**
     * Constructs a list containing the elements of the specified collection.
     *
     * @param collection the collection whose elements are to be placed into this list
     */
    public CommandList(@Nonnull Collection<CommandCallable> collection) {
        addAll(collection);
    }

    /**
     * Get a list of all {@link CommandCallable}s having the same name as the one provided. If there are no commands with the given
     * name, an empty list gets returned.
     *
     * @param name The name used for filtering
     * @return a possibly-empty mutable list of all commands with the same name as provided
     */
    public List<CommandCallable> getByName(@Nullable String name) {
        return stream().filter(commandCallable -> commandCallable.getName().equals(name)).collect(Collectors.toList());
    }

    /**
     * Get a list of all {@link CommandCallable}s having the same category as the one provided. If there are no commands with the given
     * category, an empty list gets returned.
     *
     * @param category The category used for filtering
     * @return a possibly-empty mutable list of all commands with the same category as provided
     */
    public List<CommandCallable> getByCategory(@Nullable String category) {
        return stream().filter(commandCallable -> commandCallable.getCategory().equals(category)).collect(Collectors.toList());
    }

    /**
     * Get a {@link CommandCallable} that has a label as the one provided. If there is command with the given
     * label, this returns null.
     *
     * @param label The label to find a command for
     * @return the command having the given label or {@code null} if no command was found
     */
    @Nullable
    public CommandCallable getByLabel(@Nonnull String label) {
        return stream().filter(commandCallable -> commandCallable.getLabels().contains(label)).findFirst().orElse(null);
    }

    /**
     * Sorts all {@link CommandCallable}s of this list using their category. All commands with the same category are
     * stored in the same list. Every list gets stored inside a {@code Map}. The name of the category
     * is thereby the key.
     *
     * @return a {@code Map} containing all commands sorted by their category
     * @see Command#category()
     */
    public Map<String, List<CommandCallable>> getSortedByCategories() {
        Map<String, List<CommandCallable>> sortedByCategories = new HashMap<>();
        forEach(commandCallable -> {
            if (sortedByCategories.containsKey(commandCallable.getCategory())) {
                sortedByCategories.get(commandCallable.getCategory()).add(commandCallable);
            } else {
                sortedByCategories.put(commandCallable.getCategory(), new ArrayList<CommandCallable>() {{
                    add(commandCallable);
                }});
            }
        });
        return sortedByCategories;
    }

}
