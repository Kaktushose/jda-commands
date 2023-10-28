package com.github.kaktushose.jda.commands.dispatching.interactions.contextmenu;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericParser;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class ContextMenuParser extends GenericParser<GenericContextInteractionEvent<?>> {

    @Override
    public @NotNull GenericContext<? extends GenericInteractionCreateEvent> parse(@NotNull GenericContextInteractionEvent<?> event, @NotNull JDACommands jdaCommands) {
        return new ContextMenuContext(event, jdaCommands);
    }
}
