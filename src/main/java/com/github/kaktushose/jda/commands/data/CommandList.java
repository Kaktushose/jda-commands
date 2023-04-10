package com.github.kaktushose.jda.commands.data;

import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@link ArrayList} implementation to sort and structure a collection of {@link CommandDefinition}s.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 1.0.0
 */
public class CommandList extends ArrayList<CommandDefinition> {

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
    public CommandList(@NotNull Collection<CommandDefinition> collection) {
        addAll(collection);
    }

    /**
     * Get a list of all {@link CommandDefinition}s having the same name as the one provided. If there are no commands with the given
     * name, an empty list gets returned.
     *
     * @param name The name used for filtering
     * @return a possibly-empty mutable list of all commands with the same name as provided
     */
    public List<CommandDefinition> getByName(@Nullable String name) {
        return stream().filter(command -> command.getName().equalsIgnoreCase(name)).collect(Collectors.toList());
    }

    /**
     * Get a {@link CommandDefinition} that has a label as the one provided. If there is command with the given
     * label, this returns null.
     *
     * @param label The label to find a command for
     * @return the command having the given label or {@code null} if no command was found
     */
    @Nullable
    public CommandDefinition getByLabel(@NotNull String label) {
        return stream().filter(command -> command.getName().contains(label)).findFirst().orElse(null);
    }

}
