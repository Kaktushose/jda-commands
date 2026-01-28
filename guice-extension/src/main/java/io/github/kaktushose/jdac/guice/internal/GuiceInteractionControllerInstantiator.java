package io.github.kaktushose.jdac.guice.internal;

import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.dispatching.instance.InteractionControllerInstantiator;
import io.github.kaktushose.jdac.guice.internal.guice.PerInteractionModule;
import io.github.kaktushose.jdac.guice.internal.guice.RuntimeBoundScope;
import io.github.kaktushose.jdac.introspection.Introspection;
import com.google.inject.Injector;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class GuiceInteractionControllerInstantiator implements InteractionControllerInstantiator {

    private final RuntimeBoundScope scope;
    private final Injector injector;

    public GuiceInteractionControllerInstantiator(RuntimeBoundScope scope, Injector injector) {
        this.scope = scope;
        this.injector = injector.createChildInjector(binder -> {
            binder.bindScope(Interaction.class, scope);
        });
    }

    @Override
    public <T> T instance(Class<T> clazz, Introspection introspection) {
        Injector childInjector = injector.createChildInjector(new PerInteractionModule(introspection));
        return childInjector.getInstance(clazz);
    }

    RuntimeBoundScope scope() {
        return scope;
    }
}
