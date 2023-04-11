package com.github.kaktushose.jda.commands.dispatching.buttons;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.GenericParser;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class ButtonParser extends GenericParser<ButtonInteractionEvent> {

    @Override
    public @NotNull GenericContext<? extends GenericInteractionCreateEvent> parse(@NotNull ButtonInteractionEvent event, @NotNull JDACommands jdaCommands) {
        return new ButtonContext(event, jdaCommands);
    }
}
