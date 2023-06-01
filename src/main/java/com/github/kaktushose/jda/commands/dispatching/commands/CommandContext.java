package com.github.kaktushose.jda.commands.dispatching.commands;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link GenericContext} for {@link SlashCommandInteractionEvent}.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 4.0.0
 */
public class CommandContext extends GenericContext<SlashCommandInteractionEvent> {

    private String[] input;
    private List<OptionMapping> options;
    private CommandDefinition command;
    private List<Object> arguments;

    /**
     * Constructs a new CommandContext.
     *
     * @param event       the corresponding {@link GenericInteractionCreateEvent}
     * @param jdaCommands the corresponding {@link JDACommands} instance
     */
    public CommandContext(SlashCommandInteractionEvent event, JDACommands jdaCommands) {
        super(event, jdaCommands);
    }

    /**
     * Gets the raw user input. Will be empty in phase
     * {@link com.github.kaktushose.jda.commands.dispatching.filter.FilterRegistry.FilterPosition#BEFORE_ROUTING
     * FilterPosition.BEFORE_ROUTING}.
     *
     * @return the raw user input
     * @see #getOptions()
     */
    @NotNull
    public String[] getInput() {
        return input;
    }

    /**
     * Set the user input.
     *
     * @param input the user input
     * @return the current CommandContext instance
     */
    @NotNull
    public CommandContext setInput(@NotNull String[] input) {
        this.input = input;
        return this;
    }

    /**
     * Gets the {@link OptionMapping OptionMappings}.
     *
     * @return the {@link OptionMapping OptionMappings}
     * @see #getInput()
     */
    @NotNull
    public List<OptionMapping> getOptions() {
        return options;
    }

    /**
     * Gets the {@link OptionMapping OptionMappings} inserted in a {@code Map<Name, Mapping>}.
     *
     * @return the {@link OptionMapping OptionMappings}
     * @see #getInput()
     */
    @NotNull
    public Map<String, OptionMapping> getOptionsAsMap() {
        Map<String, OptionMapping> result = new HashMap<>();
        options.forEach(option -> result.put(option.getName(), option));
        return result;
    }

    /**
     * Set the {@link OptionMapping OptionMappings}.
     *
     * @param options the {@link OptionMapping OptionMappings}
     * @return the current CommandContext instance
     */
    @NotNull
    public CommandContext setOptions(@NotNull List<OptionMapping> options) {
        this.options = options;
        return this;
    }

    /**
     * Gets the {@link CommandDefinition}. This will return null until the command got routed.
     *
     * @return the {@link CommandDefinition}
     */
    @Nullable
    public CommandDefinition getCommand() {
        return command;
    }

    /**
     * Set the {@link CommandDefinition}.
     *
     * @param command the {@link CommandDefinition}
     * @return the current CommandContext instance
     */
    @NotNull
    public CommandContext setCommand(@Nullable CommandDefinition command) {
        this.command = command;
        return this;
    }

    /**
     * Gets the parsed arguments.
     *
     * @return the parsed arguments
     */
    @NotNull
    public List<Object> getArguments() {
        return arguments;
    }

    /**
     * Set the arguments.
     *
     * @param arguments the parsed arguments
     * @return the current CommandContext instance
     */
    @NotNull
    public CommandContext setArguments(@NotNull List<Object> arguments) {
        this.arguments = arguments;
        return this;
    }
}
