package com.github.kaktushose.jda.commands.dispatching.interactions.modals;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.interactions.GenericParser;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class ModalParser extends GenericParser<ModalInteractionEvent> {

    @Override
    public @NotNull GenericContext<? extends GenericInteractionCreateEvent> parse(@NotNull ModalInteractionEvent event, @NotNull JDACommands jdaCommands) {
        return new ModalContext(event, jdaCommands);
    }
}
