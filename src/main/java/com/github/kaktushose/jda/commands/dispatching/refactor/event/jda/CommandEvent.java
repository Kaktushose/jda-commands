package com.github.kaktushose.jda.commands.dispatching.refactor.event.jda;

import com.github.kaktushose.jda.commands.dispatching.refactor.event.JDAEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public sealed interface CommandEvent<T extends GenericCommandInteractionEvent> extends JDAEvent<T> {
    record SlashCommandEvent(SlashCommandInteractionEvent event) implements CommandEvent<SlashCommandInteractionEvent> {}
    record ContextCommandEvent<T>(GenericContextInteractionEvent<T> event) implements CommandEvent<GenericContextInteractionEvent<?>> {}
}
