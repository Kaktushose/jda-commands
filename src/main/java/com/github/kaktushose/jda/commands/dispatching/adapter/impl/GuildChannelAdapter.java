package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.Helpers;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for JDAs {@link GuildChannel}.
 *
 * @since 2.3.0
 */
public class GuildChannelAdapter implements TypeAdapter<GuildChannel> {

    /**
     * Attempts to parse a String to a {@link GuildChannel}. Accepts both the channel id and name.
     *
     * @param raw   the String to parse
     * @param event the {@link GenericInteractionCreateEvent}
     * @return the parsed {@link GuildChannel} or an empty Optional if the parsing fails
     */
    @NotNull
    @Override
    public Optional<GuildChannel> apply(@NotNull String raw, @NotNull GenericInteractionCreateEvent event) {
        Channel channel = event.getChannel();
        if (channel == null) {
            return Optional.empty();
        }

        return Helpers.resolveGuildChannel(raw, event);
    }
}
