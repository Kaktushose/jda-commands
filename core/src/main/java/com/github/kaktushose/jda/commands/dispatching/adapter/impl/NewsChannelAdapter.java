package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import java.util.Optional;

/// Type adapter for JDAs [NewsChannel].
public class NewsChannelAdapter implements TypeAdapter<NewsChannel> {

    /// Attempts to parse a String to a [NewsChannel]. Accepts both the channel id and name.
    ///
    /// @param raw   the String to parse
    /// @param event the [GenericInteractionCreateEvent]
    /// @return the parsed [NewsChannel] or an empty Optional if the parsing fails
    
    @Override
    public Optional<NewsChannel> apply(String raw, GenericInteractionCreateEvent event) {
        Channel channel = event.getChannel();
        if (channel == null) {
            return Optional.empty();
        }

        return Helpers.resolveGuildChannel(raw, event).filter(NewsChannel.class::isInstance).map(NewsChannel.class::cast);
    }
}
