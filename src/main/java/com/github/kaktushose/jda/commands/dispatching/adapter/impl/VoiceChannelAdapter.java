package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.Helpers;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for JDAs {@link VoiceChannel}.
 *
 * @since 2.3.0
 */
public class VoiceChannelAdapter implements TypeAdapter<VoiceChannel> {

    /**
     * Attempts to parse a String to a {@link VoiceChannel}. Accepts both the channel id and name.
     *
     * @param raw   the String to parse
     * @param event the {@link Context}
     * @return the parsed {@link VoiceChannel} or an empty Optional if the parsing fails
     */
    @Override
    public Optional<VoiceChannel> apply(@NotNull String raw, @NotNull GenericInteractionCreateEvent event) {
        Channel channel = event.getChannel();
        if (channel == null) {
            return Optional.empty();
        }

        return Helpers.resolveGuildChannel(event, raw).filter(VoiceChannel.class::isInstance).map(VoiceChannel.class::cast);
    }
}
