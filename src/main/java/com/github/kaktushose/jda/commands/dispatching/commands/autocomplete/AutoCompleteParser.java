package com.github.kaktushose.jda.commands.dispatching.commands.autocomplete;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.GenericParser;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class AutoCompleteParser extends GenericParser<CommandAutoCompleteInteractionEvent> {

    @Override
    public @NotNull GenericContext<? extends GenericInteractionCreateEvent> parse(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull JDACommands jdaCommands) {
        return new AutoCompleteContext(event, jdaCommands);
    }
}
