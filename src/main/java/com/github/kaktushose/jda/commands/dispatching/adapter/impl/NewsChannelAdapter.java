package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for JDAs {@link net.dv8tion.jda.api.entities.NewsChannel}.
 *
 * @author Kaktushose
 * @version 2.3.0
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
    public Optional<NewsChannel> parse(@NotNull String raw, @NotNull Context context) {
        Channel channel = context.getEvent().getChannel();
        if (channel == null) {
            return Optional.empty();
        }

        NewsChannel newsChannel;
        raw = sanitizeMention(raw);

        Guild guild = context.getEvent().getGuild();
        if (raw.matches("\\d+")) {
            newsChannel = guild.getNewsChannelById(raw);
        } else {
            newsChannel = guild.getNewsChannelsByName(raw, true).stream().findFirst().orElse(null);
        }
        if (newsChannel == null) {
            return Optional.empty();
        }
        return Optional.of(newsChannel);
    }
}
