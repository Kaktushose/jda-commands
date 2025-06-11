package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import java.util.Optional;

/// Type adapter for JDAs [StageChannel].
public class StageChannelAdapter implements TypeAdapter<StageChannel> {

    /// Attempts to parse a String to a [StageChannel]. Accepts both the channel id and name.
    ///
    /// @param raw   the String to parse
    /// @param event the [GenericInteractionCreateEvent]
    /// @return the parsed [StageChannel] or an empty Optional if the parsing fails
    
    @Override
    public Optional<StageChannel> apply(String raw, GenericInteractionCreateEvent event) {
        Channel channel = event.getChannel();
        if (channel == null) {
            return Optional.empty();
        }

        return Helpers.resolveGuildChannel(raw, event).filter(StageChannel.class::isInstance).map(StageChannel.class::cast);
    }
}
