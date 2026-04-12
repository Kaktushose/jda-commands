package io.github.kaktushose.jdac.guice.internal.guice.modules;

import com.google.inject.Provides;
import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.introspection.Introspection;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class RuntimeScopeModule extends InitializedScopeModule {

    // stage = RUNTIME
    public RuntimeScopeModule(Introspection introspection) {
        super(introspection);
    }

    @Provides
    public JDA jda() {
        return introspection.get(Property.JDA);
    }

}
