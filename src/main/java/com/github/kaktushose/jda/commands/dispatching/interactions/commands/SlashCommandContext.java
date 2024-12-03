package com.github.kaktushose.jda.commands.dispatching.interactions.commands;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link Context} for {@link SlashCommandInteractionEvent}.
 *
 * @since 4.0.0
 */
public class SlashCommandContext extends Context {

    private String[] input;
    private List<OptionMapping> options;
    private SlashCommandDefinition command;
    private List<Object> arguments;

    /**
     * Constructs a new CommandContext.
     *
     * @param event       the corresponding {@link SlashCommandInteractionEvent}
     */
    public SlashCommandContext(SlashCommandInteractionEvent event, InteractionRegistry interactionRegistry, ImplementationRegistry implementationRegistry) {
        super(event, interactionRegistry, implementationRegistry);
        setOptions(event.getOptions());
    }

    @Override
    public SlashCommandInteractionEvent getEvent() {
        return (SlashCommandInteractionEvent) super.getEvent();
    }

    /**
     * Gets the raw user input.
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
    public SlashCommandContext setInput(@NotNull String[] input) {
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
     * Set the {@link OptionMapping OptionMappings}.
     *
     * @param options the {@link OptionMapping OptionMappings}
     * @return the current CommandContext instance
     */
    @NotNull
    public SlashCommandContext setOptions(@NotNull List<OptionMapping> options) {
        this.options = options;
        return this;
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
     * Gets the {@link SlashCommandDefinition}. This will return null until the command got routed.
     *
     * @return the {@link SlashCommandDefinition}
     */
    @Nullable
    public SlashCommandDefinition getCommand() {
        return command;
    }

    /**
     * Set the {@link SlashCommandDefinition}.
     *
     * @param command the {@link SlashCommandDefinition}
     * @return the current CommandContext instance
     */
    @NotNull
    public SlashCommandContext setCommand(@Nullable SlashCommandDefinition command) {
        this.command = command;
        setInteractionDefinition(command);
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
    public SlashCommandContext setArguments(@NotNull List<Object> arguments) {
        this.arguments = arguments;
        return this;
    }
}
