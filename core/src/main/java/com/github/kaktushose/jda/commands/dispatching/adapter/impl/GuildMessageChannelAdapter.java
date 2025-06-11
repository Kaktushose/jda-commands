package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import java.util.Optional;

/// Type adapter for JDAs [GuildMessageChannel].
public class GuildMessageChannelAdapter implements TypeAdapter<GuildMessageChannel> {

    /// Attempts to parse a String to a [GuildMessageChannel]. Accepts both the channel id and name.
    ///
    /// @param raw   the String to parse
    /// @param event the [GenericInteractionCreateEvent]
    /// @return the parsed [GuildMessageChannel] or an empty Optional if the parsing fails
    
    @Override
    public Optional<GuildMessageChannel> apply(String raw, GenericInteractionCreateEvent event) {
        Channel channel = event.getChannel();
        if (channel == null) {
            return Optional.empty();
        }

        return Helpers.resolveGuildChannel(raw, event)
                .filter(it -> it.getType().isMessage())
                .map(GuildMessageChannel.class::cast);
    }
}
