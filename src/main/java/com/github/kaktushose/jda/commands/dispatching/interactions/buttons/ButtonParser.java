package com.github.kaktushose.jda.commands.dispatching.interactions.buttons;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericParser;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of {@link GenericParser} that can parse {@link ButtonInteractionEvent}.
 *
 * @author Kaktushose
 * @version 4.0.0
 * @since 4.0.0
 */
public class ButtonParser extends GenericParser<ButtonInteractionEvent> {

    /**
     * Takes a {@link ButtonInteractionEvent}, parses and transpiles it into a {@link ButtonContext}.
     *
     * @param event       the {@link SlashCommandInteractionEvent} to parse
     * @param jdaCommands the {@link JDACommands} instance
     * @return a new {@link ButtonContext}
     */
    @Override
    public @NotNull GenericContext<? extends GenericInteractionCreateEvent> parse(@NotNull ButtonInteractionEvent event, @NotNull JDACommands jdaCommands) {
        return new ButtonContext(event, jdaCommands);
    }
}
