package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for JDAs {@link AudioChannel}.
 *
 * @since 2.3.0
 */
public class AudioChannelAdapter implements TypeAdapter<AudioChannel> {

    /**
     * Attempts to parse a String to a {@link AudioChannel}. Accepts both the channel id and name.
     *
     * @param raw   the String to parse
     * @param event the {@link GenericInteractionCreateEvent}
     * @return the parsed {@link AudioChannel} or an empty Optional if the parsing fails
     */
    @NotNull
    @Override
    public Optional<AudioChannel> apply(@NotNull String raw, @NotNull GenericInteractionCreateEvent event) {
        Channel channel = event.getChannel();
        if (channel == null) {
            return Optional.empty();
        }

        return Helpers.resolveGuildChannel(raw, event)
                .filter(it -> it.getType().isAudio())
                .map(AudioChannel.class::cast);
    }
}
