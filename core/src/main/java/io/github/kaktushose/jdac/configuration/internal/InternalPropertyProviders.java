package io.github.kaktushose.jdac.configuration.internal;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.internal.JDAContext;

public interface InternalPropertyProviders {

    Property<Embeds.Configuration> EMBED_CONFIG_IMPL =
            new Property.Singleton<>("EMBED_CONFIG", Property.Category.PROVIDED, Embeds.Configuration.class);

    Property<JDAContext> JDA_CONTEXT =
            new Property.Singleton<>("JDA_CONTEXT", Property.Category.PROVIDED, JDAContext.class);

    Property<Embeds> EMBEDS =
            new Property.Singleton<>("EMBEDS", Property.Category.PROVIDED, Embeds.class);
}
