package com.github.kaktushose.jda.commands.data;

import com.github.kaktushose.jda.commands.annotations.SlashCommand;
import com.github.kaktushose.jda.commands.reflect.interactions.SlashCommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An {@link ArrayList} implementation to sort and structure a collection of {@link SlashCommandDefinition}s.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 1.0.0
 */
public class CommandList extends ArrayList<SlashCommandDefinition> {

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
    public CommandList(@NotNull Collection<SlashCommandDefinition> collection) {
        addAll(collection);
    }

    /**
     * Get a list of all {@link SlashCommandDefinition}s having the same name as the one provided. If there are no commands with the given
     * name, an empty list gets returned.
     *
     * @param name The name used for filtering
     * @return a possibly-empty mutable list of all commands with the same name as provided
     */
    public List<SlashCommandDefinition> getByName(@Nullable String name) {
        return stream().filter(command -> command.getMetadata().getName().equalsIgnoreCase(name)).collect(Collectors.toList());
    }

    /**
     * Get a list of all {@link SlashCommandDefinition}s having the same category as the one provided. If there are no commands with the given
     * category, an empty list gets returned.
     *
     * @param category The category used for filtering
     * @return a possibly-empty mutable list of all commands with the same category as provided
     */
    public List<SlashCommandDefinition> getByCategory(@Nullable String category) {
        return stream().filter(command -> command.getMetadata().getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());
    }

    /**
     * Get a {@link SlashCommandDefinition} that has a label as the one provided. If there is command with the given
     * label, this returns null.
     *
     * @param label The label to find a command for
     * @return the command having the given label or {@code null} if no command was found
     */
    @Nullable
    public SlashCommandDefinition getByLabel(@Nullable String label) {
        return stream().filter(command -> command.getLabel().contains(label)).findFirst().orElse(null);
    }

    /**
     * Sorts all {@link SlashCommandDefinition}s of this list using their category. All commands with the same category are
     * stored in the same list. All lists gets stored inside a {@code Map}. The name of the category
     * is thereby the key.
     *
     * @return a {@code Map} containing all commands sorted by their category
     * @see SlashCommand#category()
     */
    public Map<String, List<SlashCommandDefinition>> getSortedByCategories() {
        Map<String, List<SlashCommandDefinition>> sortedByCategories = new HashMap<>();
        forEach(command -> {
            if (sortedByCategories.containsKey(command.getMetadata().getCategory())) {
                sortedByCategories.get(command.getMetadata().getCategory()).add(command);
            } else {
                sortedByCategories.put(command.getMetadata().getCategory(), new ArrayList<SlashCommandDefinition>() {{
                    add(command);
                }});
            }
        });

        sortedByCategories.forEach((category, commands) -> {
            List<SlashCommandDefinition> sorted = commands.stream().sorted().collect(Collectors.toList());
            sortedByCategories.put(category, sorted);
        });

        return sortedByCategories;
    }
}
