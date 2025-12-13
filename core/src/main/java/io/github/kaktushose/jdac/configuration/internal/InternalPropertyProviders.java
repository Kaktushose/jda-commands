package io.github.kaktushose.jdac.configuration.internal;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.Property.Category;
import io.github.kaktushose.jdac.configuration.Property.Singleton;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.internal.JDAContext;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface InternalPropertyProviders {

    Property<Embeds.Configuration> EMBED_CONFIG_INTERNAL = new Singleton<>("EMBED_CONFIG_INTERNAL", Category.PROVIDED, Embeds.Configuration.class);

    Property<JDAContext> JDA_CONTEXT = new Singleton<>("JDA_CONTEXT", Category.PROVIDED, JDAContext.class);

    Property<Embeds> EMBEDS = new Singleton<>("EMBEDS", Category.PROVIDED, Embeds.class);
}
