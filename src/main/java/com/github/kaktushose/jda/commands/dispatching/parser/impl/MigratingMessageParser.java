package com.github.kaktushose.jda.commands.dispatching.parser.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.CommandDispatcher;
import com.github.kaktushose.jda.commands.dispatching.parser.Parser;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.interactions.commands.CommandRegistrationPolicy;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of {@link Parser} that can parse {@link MessageReceivedEvent}.
 * This parser will only be used if the
 * {@link CommandRegistrationPolicy CommandRegistrationPolicy} is set to
 * {@link CommandRegistrationPolicy#MIGRATING
 * CommandRegistrationPolicy.MIGRATING}. It will make any text command fail but respond with a deprecation message.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see CommandRegistrationPolicy CommandRegistrationPolicy
 * @see ErrorMessageFactory#getSlashCommandMigrationMessage(CommandContext)
 * @since 2.3.0
 */
public class MigratingMessageParser extends Parser<MessageReceivedEvent> {

    @Override
    public @NotNull CommandContext parse(@NotNull MessageReceivedEvent event, @NotNull CommandDispatcher dispatcher) {
        ImplementationRegistry registry = dispatcher.getImplementationRegistry();
        GuildSettings settings = registry.getSettingsProvider().getSettings(event.isFromGuild() ? event.getGuild() : null);
        ErrorMessageFactory errorMessageFactory = registry.getErrorMessageFactory();
        CommandContext context = new CommandContext(event, dispatcher.getJdaCommands(), settings, registry);

        if (event.getAuthor().isBot() && settings.isIgnoreBots()) {
            return context.setCancelled(true);
        }

        if (event.getMessage().getContentRaw().startsWith(settings.getPrefix())) {
            context.setErrorMessage(errorMessageFactory.getSlashCommandMigrationMessage(context));
        }

        return context.setCancelled(true);
    }
}
