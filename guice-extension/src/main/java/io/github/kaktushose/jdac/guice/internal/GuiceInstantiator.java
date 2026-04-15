package io.github.kaktushose.jdac.guice.internal;

import com.google.inject.Injector;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.dispatching.instance.Instantiator;
import io.github.kaktushose.jdac.guice.internal.guice.RuntimeBoundScope;
import io.github.kaktushose.jdac.guice.internal.guice.modules.InitializedScopeModule;
import io.github.kaktushose.jdac.guice.internal.guice.modules.RuntimeScopeModule;
import io.github.kaktushose.jdac.property.JDACIntrospection;
import io.github.kaktushose.jdac.property.JDACScope;
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
    public <T> T instance(Class<T> clazz, JDACIntrospection introspection) {
        JDACScope scope = introspection.scope();

        Injector scoped = switch (scope) {
            case RUNTIME -> injector.createChildInjector(new RuntimeScopeModule(introspection)); // runtime -> create interaction controller
            case INITIALIZED -> injector.createChildInjector(new InitializedScopeModule(introspection)); // option data: choices provider method
            default -> throw new UnsupportedOperationException("Unsupported scope of introspection: %s".formatted(scope));
        };

        return scoped.getInstance(clazz);
    }
}
