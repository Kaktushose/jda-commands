package io.github.kaktushose.jdac.guice.internal;

import com.google.inject.Injector;
import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.dispatching.instance.InteractionControllerInstantiator;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.message.i18n.I18n;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class GuiceInteractionControllerInstantiator implements InteractionControllerInstantiator {

    private final I18n i18n;
    private final Injector injector;

    public GuiceInteractionControllerInstantiator(I18n i18n, Injector injector) {
        this.i18n = i18n;
        this.injector = injector;
    }

    @Override
    public <T> T instance(Class<T> clazz, Introspection context) {
        Injector childInjector = injector.createChildInjector(new InteractionControllerInstantiatorModule(i18n, context.get(Property.JDA)));
        return childInjector.getInstance(clazz);
    }
}
