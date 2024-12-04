package com.github.kaktushose.jda.commands.dispatching.adapter.impl;

import com.github.kaktushose.jda.commands.Helpers;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapter;
import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Type adapter for JDAs {@link TextChannel}.
 *
 * @since 2.0.0
 */
public class TextChannelAdapter implements TypeAdapter<TextChannel> {

    /**
     * Attempts to parse a String to a {@link TextChannel}. Accepts both the channel id and name.
     *
     * @param raw     the String to parse
     * @param context the {@link Context}
     * @return the parsed {@link TextChannel} or an empty Optional if the parsing fails
     */
    @Override
    public Optional<TextChannel> apply(@NotNull String raw, @NotNull Context context) {
        if (context.getEvent().getGuild() == null) {
            return Optional.empty();
        }

        return Helpers.resolveGuildChannel(context, raw).filter(TextChannel.class::isInstance).map(TextChannel.class::cast);
    }
}
