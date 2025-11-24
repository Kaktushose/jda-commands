package io.github.kaktushose.jdac.configuration.internal;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.internal.JDAContext;

public interface InternalPropertyProviders {

    /// only user settable
    Property<Embeds.Configuration> EMBED_CONFIG =
            new Property.Singleton<>("EMBED_CONFIG", Property.Category.USER, Embeds.Configuration.class);

    /// only user settable
    Property<ExtensionFilter> EXTENSION_FILTER =
            new Property.Singleton<>("EXTENSION_FILTER", Property.Category.USER, ExtensionFilter.class);

    /// only created
    Property<JDAContext> JDA_CONTEXT =
            new Property.Singleton<>("JDA_CONTEXT", Property.Category.PROVIDED, JDAContext.class);

    Property<Embeds> EMBEDS =
            new Property.Singleton<>("EMBEDS", Property.Category.PROVIDED, Embeds.class);
}
