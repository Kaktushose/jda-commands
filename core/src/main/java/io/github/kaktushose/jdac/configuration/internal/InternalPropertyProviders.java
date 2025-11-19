package io.github.kaktushose.jdac.configuration.internal;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.internal.JDAContext;

public interface InternalPropertyProviders {
    /// only user settable
    Property<Embeds.Configuration> EMBED_CONFIG =
            new Property.Instance<>("EMEBED_CONFIG", Property.Scope.USER, Embeds.Configuration.class);

    Property<ExtensionFilter> EXTENSION_FILTER =
            new Property.Instance<>("EXTENSION_FILTER", Property.Scope.USER, ExtensionFilter.class);

    /// only created
    Property<JDAContext> JDA_CONTEXT =
            new Property.Instance<>("JDA_CONTEXT", Property.Scope.PROVIDED, JDAContext.class);

    Property<Embeds> EMBEDS =
            new Property.Instance<>("EMBEDS", Property.Scope.PROVIDED, Embeds.class);
}
