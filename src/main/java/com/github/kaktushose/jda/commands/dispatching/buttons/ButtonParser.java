package com.github.kaktushose.jda.commands.dispatching.buttons;

import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.dispatching.GenericContext;
import com.github.kaktushose.jda.commands.dispatching.GenericParser;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class ButtonParser extends GenericParser<ButtonInteractionEvent> {

    @Override
    public @NotNull GenericContext<? extends GenericInteractionCreateEvent> parse(@NotNull ButtonInteractionEvent event, @NotNull JDACommands jdaCommands) {
        ImplementationRegistry registry = jdaCommands.getImplementationRegistry();
        GuildSettings settings = registry.getSettingsProvider().getSettings(event.isFromGuild() ? event.getGuild() : null);
        ErrorMessageFactory errorMessageFactory = registry.getErrorMessageFactory();

        ButtonContext context = new ButtonContext(event, jdaCommands, settings, registry);

        if (settings.isMutedGuild()) {
            //context.setErrorMessage(errorMessageFactory.getGuildMutedMessage(context));
            return context.setCancelled(true);
        }

        if (settings.getMutedChannels().contains(event.getChannel().getIdLong())) {
            //context.setErrorMessage(errorMessageFactory.getChannelMutedMessage(context));
            return context.setCancelled(true);
        }

        return context;
    }
}
