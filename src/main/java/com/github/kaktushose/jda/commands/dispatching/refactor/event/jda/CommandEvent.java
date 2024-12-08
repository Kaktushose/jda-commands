package com.github.kaktushose.jda.commands.dispatching.refactor.event.jda;

import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public sealed interface CommandEvent extends JDAEvent {
    record SlashCommandEvent(SlashCommandInteractionEvent event) implements CommandEvent {}
    record ContextCommandEvent<T>(GenericContextInteractionEvent<T> event) implements CommandEvent {}
}
