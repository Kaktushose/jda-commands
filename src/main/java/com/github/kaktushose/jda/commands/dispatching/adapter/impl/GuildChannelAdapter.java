package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for JDAs {@link GuildChannel}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @since 2.3.0
 */
public class GuildChannelAdapter implements TypeAdapter<GuildChannel> {

    /**
     * Attempts to parse a String to a {@link GuildChannel}. Accepts both the channel id and name.
     *
     * @param raw     the String to parse
     * @param context the {@link CommandContext}
     * @return the parsed {@link GuildChannel} or an empty Optional if the parsing fails
     */
    @Override
    public Optional<GuildChannel> parse(@NotNull String raw, @NotNull CommandContext context) {
        if (!context.getEvent().isFromType(ChannelType.TEXT)) {
            return Optional.empty();
        }

        GuildChannel guildChannel;
        raw = sanitizeMention(raw);

        Guild guild = context.getEvent().getGuild();
        if (raw.matches("\\d+")) {
            guildChannel = guild.getGuildChannelById(raw);
        } else {
            String finalRaw = raw;
            guildChannel = guild.getChannels().stream().filter(channel -> channel.getName().equalsIgnoreCase(finalRaw))
                    .findFirst().orElse(null);
        }
        if (guildChannel == null) {
            return Optional.empty();
        }
        return Optional.of(guildChannel);
    }
}
