package io.github.kaktushose.jdac.guice.internal;

import com.google.inject.Injector;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.dispatching.instance.InteractionControllerInstantiator;
import io.github.kaktushose.jdac.introspection.Introspection;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class GuiceInteractionControllerInstantiator implements InteractionControllerInstantiator {

    private final Injector injector;

    public GuiceInteractionControllerInstantiator(Injector injector) {
        this.injector = injector.createChildInjector(binder -> {
            binder.bindScope(Interaction.class, new RuntimeBoundScope());
        });
    }

    @Override
    public <T> T instance(Class<T> clazz, Introspection introspection) {
        Injector childInjector = injector.createChildInjector(new PerInteractionModule(introspection));
        return childInjector.getInstance(clazz);
    }
}
