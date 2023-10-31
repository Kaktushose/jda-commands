package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for JDAs {@link AudioChannel}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @since 2.3.0
 */
public class AudioChannelAdapter implements TypeAdapter<AudioChannel> {

    /**
     * Attempts to parse a String to a {@link AudioChannel}. Accepts both the channel id and name.
     *
     * @param raw     the String to parse
     * @param context the {@link Context}
     * @return the parsed {@link AudioChannel} or an empty Optional if the parsing fails
     */
    @Override
    public Optional<AudioChannel> parse(@NotNull String raw, @NotNull Context context) {
        Channel channel = context.getEvent().getChannel();
        if (channel == null) {
            return Optional.empty();
        }

        GuildChannel guildChannel;
        raw = sanitizeMention(raw);

        Guild guild = context.getEvent().getGuild();
        if (raw.matches("\\d+")) {
            guildChannel = guild.getGuildChannelById(raw);
        } else {
            String finalRaw = raw;
            guildChannel = guild.getChannels().stream().filter(it -> it.getName().equalsIgnoreCase(finalRaw))
                    .findFirst().orElse(null);
        }
        if (guildChannel == null) {
            return Optional.empty();
        }
        if (!guildChannel.getType().isAudio()) {
            return Optional.empty();
        }
        return Optional.of((AudioChannel) guildChannel);
    }
}
