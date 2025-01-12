package com.github.kaktushose.jda.commands.dispatching.instantiation;

import java.lang.reflect.InvocationTargetException;

public class DefaultInstantiator implements Instantiator{
    @Override
    public <T> T instantiate(Class<T> clazz, InstantiationContext context) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
