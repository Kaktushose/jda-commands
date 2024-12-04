package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

class Helpers {

    /**
     * Sanitizes a String containing a raw mention. This will remove all markdown characters namely <em>< @ # & ! ></em>
     * For instance: {@code <@!393843637437464588>} gets sanitized to {@code 393843637437464588}
     *
     * @param mention the raw String to sanitize
     * @return the sanitized String
     */
    static String sanitizeMention(@NotNull String mention) {
        if (mention.matches("<[@#][&!]?([0-9]{4,})>")) {
            return mention.replaceAll("<[@#][&!]?", "").replace(">", "");
        }
        return mention;
    }

    static Optional<GuildChannel> resolveGuildChannel(Context context, String raw) {
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
        return Optional.ofNullable(guildChannel);
    }

}
