package com.github.kaktushose.jda.commands.guice.internal;

import com.github.kaktushose.jda.commands.dispatching.instance.InstanceProvider;
import com.google.inject.Injector;

public class GuiceInstanceProvider implements InstanceProvider {

    private final Injector injector;

    public GuiceInstanceProvider(Injector injector) {
        this.injector = injector;
    }

    @Override
    public <T> T instance(Class<T> clazz, Context context) {
        return injector.getInstance(clazz);
    }
}
