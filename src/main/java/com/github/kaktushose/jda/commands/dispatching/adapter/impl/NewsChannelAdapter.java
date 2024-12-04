package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.Helpers;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for JDAs {@link NewsChannel}.
 *
 * @since 2.3.0
 */
public class NewsChannelAdapter implements TypeAdapter<NewsChannel> {

    /**
     * Attempts to parse a String to a {@link NewsChannel}. Accepts both the channel id and name.
     *
     * @param raw     the String to parse
     * @param context the {@link Context}
     * @return the parsed {@link NewsChannel} or an empty Optional if the parsing fails
     */
    @Override
    public Optional<NewsChannel> apply(@NotNull String raw, @NotNull Context context) {
        Channel channel = context.getEvent().getChannel();
        if (channel == null) {
            return Optional.empty();
        }

        return Helpers.resolveGuildChannel(context, raw).filter(NewsChannel.class::isInstance).map(NewsChannel.class::cast);
    }
}
