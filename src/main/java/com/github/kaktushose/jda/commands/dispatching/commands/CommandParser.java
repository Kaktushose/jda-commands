package com.github.kaktushose.jda.commands.dispatching.commands;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.GenericParser;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * An implementation of {@link GenericParser} that can parse {@link SlashCommandInteractionEvent}.
 * This parser will work within the limitations given by the {@link GuildSettings}.
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
        ImplementationRegistry registry =jdaCommands.getImplementationRegistry();
        GuildSettings settings = registry.getSettingsProvider().getSettings(event.isFromGuild() ? event.getGuild() : null);
        ErrorMessageFactory errorMessageFactory = registry.getErrorMessageFactory();
        CommandContext context = new CommandContext(event, jdaCommands, settings, registry);

        context.setInput(event.getFullCommandName().split(" ")).setOptions(event.getOptions());

        if (settings.isMutedGuild()) {
            context.setErrorMessage(errorMessageFactory.getGuildMutedMessage(context));
            return context.setCancelled(true);
        }

        if (settings.getMutedChannels().contains(event.getChannel().getIdLong())) {
            context.setErrorMessage(errorMessageFactory.getChannelMutedMessage(context));
            return context.setCancelled(true);
        }

        if (event.getName().equals("help")) {
            return context.setHelpEvent(true).setInput(
                    event.getOptions().stream().map(OptionMapping::getAsString).flatMap(it -> Stream.of(it.split(" "))).toArray(String[]::new)
            );
        }

        return context;
    }
}
