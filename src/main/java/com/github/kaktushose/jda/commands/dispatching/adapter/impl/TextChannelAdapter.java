package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for JDAs {@link TextChannel}.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public class TextChannelAdapter implements TypeAdapter<TextChannel> {

    /**
     * Attempts to parse a String to a {@link TextChannel}. Accepts both the channel id and name.
     *
     * @param raw     the String to parse
     * @param context the {@link CommandContext}
     * @return the parsed {@link TextChannel} or an empty Optional if the parsing fails
     */
    @Override
    public Optional<TextChannel> parse(@NotNull String raw, @NotNull CommandContext context) {
        if (!context.getEvent().isFromType(ChannelType.TEXT)) {
            return Optional.empty();
        }

        TextChannel textChannel;
        raw = sanitizeMention(raw);

        Guild guild = context.getEvent().getGuild();
        if (raw.matches("\\d+")) {
            textChannel = guild.getTextChannelById(raw);
        } else {
            textChannel = guild.getTextChannelsByName(raw, true).stream().findFirst().orElse(null);
        }
        if (textChannel == null) {
            return Optional.empty();
        }
        return Optional.of(textChannel);
    }
}
