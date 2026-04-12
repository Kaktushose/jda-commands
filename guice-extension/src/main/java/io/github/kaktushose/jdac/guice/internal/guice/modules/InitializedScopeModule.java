package io.github.kaktushose.jdac.guice.internal.guice.modules;

import com.google.inject.Provides;
import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.introspection.Definitions;
import io.github.kaktushose.jdac.introspection.Introspection;

public class InitializedScopeModule extends ConfigurationScopeModule {
    public InitializedScopeModule(Introspection introspection) {
        super(introspection);
    }

    @Provides
    public JDACommands jdaCommands() {
        return introspection.get(Property.JDA_COMMANDS);
    }

    @Provides
    public Definitions definition() {
        return introspection.get(Property.DEFINITIONS);
    }
}
