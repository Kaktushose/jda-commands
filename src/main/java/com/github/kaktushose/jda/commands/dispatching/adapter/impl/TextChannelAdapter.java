package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/// Type adapter for JDAs [TextChannel].
public class TextChannelAdapter implements TypeAdapter<TextChannel> {

    /// Attempts to parse a String to a [TextChannel]. Accepts both the channel id and name.
    ///
    /// @param raw   the String to parse
    /// @param event the [GenericInteractionCreateEvent]
    /// @return the parsed [TextChannel] or an empty Optional if the parsing fails
    @NotNull
    @Override
    public Optional<TextChannel> apply(@NotNull String raw, @NotNull GenericInteractionCreateEvent event) {
        if (event.getGuild() == null) {
            return Optional.empty();
        }

        return Helpers.resolveGuildChannel(raw, event).filter(TextChannel.class::isInstance).map(TextChannel.class::cast);
    }
}
