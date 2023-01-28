package com.github.kaktushose.jda.commands.embeds.help;

import com.github.kaktushose.jda.commands.embeds.EmbedCache;

/**
 * Subtype of {@link DefaultHelpMessageFactory} that can load the embeds from an {@link EmbedCache}.
 *
 * @author Kaktushose
 * @version 2.0.0
 * @see DefaultHelpMessageFactory
 * @see EmbedCache
 * @since 2.0.0
 */
public class JsonHelpMessageFactory extends DefaultHelpMessageFactory {

    private final EmbedCache embedCache;

    public JsonHelpMessageFactory(EmbedCache embedCache) {
        this.embedCache = embedCache;
    }

    //TODO reimplement

}
