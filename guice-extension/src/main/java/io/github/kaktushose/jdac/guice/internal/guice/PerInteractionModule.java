package io.github.kaktushose.jdac.guice.internal.guice;

import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.introspection.Definitions;
import io.github.kaktushose.jdac.introspection.Introspection;
import com.google.inject.Provides;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PerInteractionModule extends PropertyProviderModule {

    public PerInteractionModule(Introspection introspection) {
        super(introspection);
    }

    @Provides
    public JDA jda() {
        return introspection.get(Property.JDA);
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
