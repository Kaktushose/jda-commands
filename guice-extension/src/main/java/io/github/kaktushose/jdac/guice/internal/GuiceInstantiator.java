package io.github.kaktushose.jdac.guice.internal;

import com.google.inject.Injector;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.dispatching.instance.Instantiator;
import io.github.kaktushose.jdac.guice.internal.guice.RuntimeBoundScope;
import io.github.kaktushose.jdac.guice.internal.guice.modules.InitializedScopeModule;
import io.github.kaktushose.jdac.guice.internal.guice.modules.RuntimeScopeModule;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.introspection.Stage;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class GuiceInstantiator implements Instantiator {

    private final Injector injector;

    public GuiceInstantiator(RuntimeBoundScope scope, Injector injector) {
        this.injector = injector.createChildInjector(binder -> {
            binder.bindScope(Interaction.class, scope);
        });
    }

    @Override
    public <T> T instance(Class<T> clazz, Introspection introspection) {
        Stage stage = introspection.currentStage();

        Injector scoped = switch (stage) {
            case RUNTIME -> injector.createChildInjector(new RuntimeScopeModule(introspection)); // runtime -> create interaction controller
            case INITIALIZED -> injector.createChildInjector(new InitializedScopeModule(introspection)); // option data: choices provider method
            default -> throw new UnsupportedOperationException("Unsupported stage of introspection: %s".formatted(introspection.currentStage()));
        };

        return scoped.getInstance(clazz);
    }
}
