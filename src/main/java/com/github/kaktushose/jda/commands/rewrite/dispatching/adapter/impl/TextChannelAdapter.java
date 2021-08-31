package com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.ParameterAdapter;
import com.github.kaktushose.jda.commands.rewrite.dispatching.CommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Optional;

public class TextChannelAdapter implements ParameterAdapter<TextChannel> {

    @Override
    public Optional<TextChannel> parse(String raw, CommandContext context) {
        TextChannel textChannel;
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
