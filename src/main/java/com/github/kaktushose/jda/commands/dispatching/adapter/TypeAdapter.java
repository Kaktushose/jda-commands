package com.github.kaktushose.jda.commands.dispatching.adapter;

import com.github.kaktushose.jda.commands.dispatching.interactions.Context;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Generic top level interface for type adapting.
 *
 * @param <T> the type the adapter parses
 * @author Kaktushose
 * @version 2.0.0
 * @since 2.0.0
 */
public interface TypeAdapter<T> {

    /**
     * Attempts to parse a String to the given type.
     *
     * @param raw     the String to parse
     * @param context the {@link Context}
     * @return the parsed type or an empty Optional if the parsing fails
     */
    Optional<T> parse(@NotNull String raw, @NotNull Context context);

    /**
     * Sanitizes a String containing a raw mention. This will remove all markdown characters namely <em>< @ # & ! ></em>
     * For instance: {@code <@!393843637437464588>} gets sanitized to {@code 393843637437464588}
     *
     * @param mention the raw String to sanitize
     * @return the sanitized String
     */
    default String sanitizeMention(@NotNull String mention) {
        if (mention.matches("<[@#][&!]?([0-9]{4,})>")) {
            return mention.replaceAll("<[@#][&!]?", "").replace(">", "");
        }
        return mention;
    }
}
