package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import java.util.Optional;

/// Type adapter for JDAs [ThreadChannel].
public class ThreadChannelAdapter implements TypeAdapter<ThreadChannel> {

    /// Attempts to parse a String to a [ThreadChannel]. Accepts both the channel id and name.
    ///
    /// @param raw   the String to parse
    /// @param event the [GenericInteractionCreateEvent]
    /// @return the parsed [ThreadChannel] or an empty Optional if the parsing fails
    
    @Override
    public Optional<ThreadChannel> apply(String raw, GenericInteractionCreateEvent event) {
        Channel channel = event.getChannel();
        if (channel == null) {
            return Optional.empty();
        }

        return Helpers.resolveGuildChannel(raw, event).filter(ThreadChannel.class::isInstance).map(ThreadChannel.class::cast);
    }
}
