package com.github.kaktushose.jda.commands.dependency;

import com.github.kaktushose.jda.commands.annotations.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class DependencyInjector {

    private final Map<Class<?>, Object> providedObjects;
    private final Map<Object, List<Field>> dependencies;
    private final Logger log = LoggerFactory.getLogger(DependencyInjector.class);

    DependencyInjector() {
        providedObjects = new HashMap<>();
        dependencies = new HashMap<>();
    }

    void addProvider(Object provider) {
        for (Method method : provider.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Produces.class)) {
                continue;
            }
            if (method.getParameterTypes().length != 0) {
                log.error("An error has occurred! Skipping Producer {}", method,
                        new IllegalArgumentException("Producer method must not have parameters!"));
                continue;
            }
            try {
                method.setAccessible(true);
                Object object = method.invoke(provider);
                providedObjects.put(object.getClass(), object);
            } catch (Exception e) {
                log.error("Unable to access method {}", method, e);
            }
        }
    }

    void addDependency(Object instance, List<Field> fields) {
        dependencies.put(instance, fields);
    }

    void inject() {
        dependencies.forEach((instance, fields) -> {
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    field.set(instance, getDependency(field.getType()));
                } catch (IllegalAccessException e) {
                    log.error("Unable to inject field {}", field, e);

                }
            }
        });
    }

    private Object getDependency(Class<?> clazz) {
        return providedObjects.get(clazz);
    }

}
