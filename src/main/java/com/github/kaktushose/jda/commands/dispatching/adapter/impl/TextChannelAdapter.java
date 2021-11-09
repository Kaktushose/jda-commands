package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Optional;

public class TextChannelAdapter implements TypeAdapter<TextChannel> {

    @Override
    public Optional<TextChannel> parse(String raw, CommandContext context) {
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
