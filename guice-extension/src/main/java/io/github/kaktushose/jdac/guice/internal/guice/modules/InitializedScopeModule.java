package io.github.kaktushose.jdac.guice.internal.guice.modules;

import com.google.inject.Provides;
import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.property.Definitions;
import io.github.kaktushose.jdac.property.JDACIntrospection;
import io.github.kaktushose.jdac.property.JDACProperty;

public class InitializedScopeModule extends ConfigurationScopeModule {
    public InitializedScopeModule(JDACIntrospection introspection) {
        super(introspection);
    }

    @Provides
    public JDACommands jdaCommands() {
        return introspection.get(JDACProperty.JDA_COMMANDS);
    }

    @Provides
    public Definitions definition() {
        return introspection.get(JDACProperty.DEFINITIONS);
    }
}
