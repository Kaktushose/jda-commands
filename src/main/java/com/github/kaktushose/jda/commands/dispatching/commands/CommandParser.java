package com.github.kaktushose.jda.commands.dispatching.commands;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.GenericParser;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import com.github.kaktushose.jda.commands.settings.SettingsProvider;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

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
        SettingsProvider provider = registry.getSettingsProvider();
        GuildSettings settings = event.getGuild() == null ? provider.getSettings(event.getGuild().getIdLong()) : provider.getDefaultSettings();
        ErrorMessageFactory errorMessageFactory = registry.getErrorMessageFactory();
        CommandContext context = new CommandContext(event, jdaCommands, settings, registry);

        context.setOptions(event.getOptions());

        if (settings.isMutedGuild()) {
            return context.setCancelled(true).setErrorMessage(errorMessageFactory.getGuildMutedMessage(context));
        }

        if (settings.getMutedChannels().contains(event.getChannel().getIdLong())) {
            return context.setCancelled(true).setErrorMessage(errorMessageFactory.getChannelMutedMessage(context));
        }

        return context;
    }
}
