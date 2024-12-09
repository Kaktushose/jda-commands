package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.Helpers;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import com.github.kaktushose.jda.commands.dispatching.refactor.context.ExecutionContext;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for JDAs {@link GuildMessageChannel}.
 *
 * @since 2.3.0
 */
public class GuildMessageChannelAdapter implements TypeAdapter<GuildMessageChannel> {

    /**
     * Attempts to parse a String to a {@link GuildMessageChannel}. Accepts both the channel id and name.
     *
     * @param raw     the String to parse
     * @param context the {@link Context}
     * @return the parsed {@link GuildMessageChannel} or an empty Optional if the parsing fails
     */
    @Override
    public Optional<GuildMessageChannel> apply(@NotNull String raw, @NotNull ExecutionContext<?, ?> context) {
        Channel channel = context.event().getChannel();
        if (channel == null) {
            return Optional.empty();
        }

        return Helpers.resolveGuildChannel(context, raw)
                .filter(it -> it.getType().isMessage())
                .map(GuildMessageChannel.class::cast);
    }
}
