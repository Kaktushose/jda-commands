package com.github.kaktushose.jda.commands.dispatching.commands;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.GenericParser;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of {@link GenericParser} that can parse {@link SlashCommandInteractionEvent}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @since 2.3.0
 */
public class CommandParser extends GenericParser<SlashCommandInteractionEvent> {

    /**
     * Takes a {@link SlashCommandInteractionEvent}, parses and transpiles it into a {@link GenericContext}.
     *
     * @param event      the {@link SlashCommandInteractionEvent} to parse
     * @param jdaCommands the {@link JDACommands} instance
     * @return a new {@link GenericContext}
     */
    @Override
    @NotNull
    public GenericContext<? extends GenericInteractionCreateEvent> parse(@NotNull SlashCommandInteractionEvent event, @NotNull JDACommands jdaCommands) {
        return new CommandContext(event, jdaCommands).setOptions(event.getOptions());
    }
}
