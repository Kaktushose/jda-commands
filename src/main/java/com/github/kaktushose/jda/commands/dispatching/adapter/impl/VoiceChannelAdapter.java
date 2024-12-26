package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/// Type adapter for JDAs [VoiceChannel].
public class VoiceChannelAdapter implements TypeAdapter<VoiceChannel> {

    /// Attempts to parse a String to a [VoiceChannel]. Accepts both the channel id and name.
    ///
    /// @param raw   the String to parse
    /// @param event the [GenericInteractionCreateEvent]
    /// @return the parsed [VoiceChannel] or an empty Optional if the parsing fails
    @NotNull
    @Override
    public Optional<VoiceChannel> apply(@NotNull String raw, @NotNull GenericInteractionCreateEvent event) {
        Channel channel = event.getChannel();
        if (channel == null) {
            return Optional.empty();
        }

        return Helpers.resolveGuildChannel(raw, event).filter(VoiceChannel.class::isInstance).map(VoiceChannel.class::cast);
    }
}
