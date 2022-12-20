package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for JDAs {@link net.dv8tion.jda.api.entities.VoiceChannel}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @since 2.3.0
 */
public class VoiceChannelAdapter implements TypeAdapter<VoiceChannel> {

    /**
     * Attempts to parse a String to a {@link VoiceChannel}. Accepts both the channel id and name.
     *
     * @param raw     the String to parse
     * @param context the {@link CommandContext}
     * @return the parsed {@link VoiceChannel} or an empty Optional if the parsing fails
     */
    @Override
    public Optional<VoiceChannel> parse(@NotNull String raw, @NotNull CommandContext context) {
        if (!context.getEvent().isFromType(ChannelType.TEXT)) {
            return Optional.empty();
        }

        VoiceChannel voiceChannel;
        raw = sanitizeMention(raw);

        Guild guild = context.getEvent().getGuild();
        if (raw.matches("\\d+")) {
            voiceChannel = guild.getVoiceChannelById(raw);
        } else {
            voiceChannel = guild.getVoiceChannelsByName(raw, true).stream().findFirst().orElse(null);
        }
        if (voiceChannel == null) {
            return Optional.empty();
        }
        return Optional.of(voiceChannel);
    }
}
