package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.internal.Helpers;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for JDAs {@link StageChannel}.
 *
 * @since 2.3.0
 */
public class StageChannelAdapter implements TypeAdapter<StageChannel> {

    /**
     * Attempts to parse a String to a {@link StageChannel}. Accepts both the channel id and name.
     *
     * @param raw   the String to parse
     * @param event the {@link GenericInteractionCreateEvent}
     * @return the parsed {@link StageChannel} or an empty Optional if the parsing fails
     */
    @NotNull
    @Override
    public Optional<StageChannel> apply(@NotNull String raw, @NotNull GenericInteractionCreateEvent event) {
        Channel channel = event.getChannel();
        if (channel == null) {
            return Optional.empty();
        }

        return Helpers.resolveGuildChannel(raw, event).filter(StageChannel.class::isInstance).map(StageChannel.class::cast);
    }
}
