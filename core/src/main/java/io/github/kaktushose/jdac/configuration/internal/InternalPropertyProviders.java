package io.github.kaktushose.jdac.configuration.internal;

import io.github.kaktushose.jdac.configuration.PropertyType;
import io.github.kaktushose.jdac.configuration.type.Instance;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.internal.JDAContext;

public interface InternalPropertyProviders {
    /// only user settable
    PropertyType<Embeds.Configuration> EMBED_CONFIG =
            new Instance<>("EMEBED_CONFIG", PropertyType.Scope.USER, Embeds.Configuration.class);

    PropertyType<ExtensionFilter> EXTENSION_FILTER =
            new Instance<>("EXTENSION_FILTER", PropertyType.Scope.USER, ExtensionFilter.class);

    /// only created
    PropertyType<JDAContext> JDA_CONTEXT =
            new Instance<>("JDA_CONTEXT", PropertyType.Scope.PROVIDED, JDAContext.class);

    PropertyType<Embeds> EMBEDS =
            new Instance<>("EMBEDS", PropertyType.Scope.PROVIDED, Embeds.class);
}
