package com.github.kaktushose.jda.commands.dependency;

import com.github.kaktushose.jda.commands.annotations.Produces;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Central registry for dependencies and producing methods. Registered dependencies will be injected with the
 * corresponding values if present, else null.
 *
 * @see Produces
 * @see com.github.kaktushose.jda.commands.annotations.Inject Inject
 * @since 1.0.0
 */
public class DependencyInjector {

    private final Map<Class<?>, Object> providedObjects;
    private final Logger log = LoggerFactory.getLogger(DependencyInjector.class);
    private final Map<Class<?>, List<Field>> dependencies;

    /**
     * Create a new DependencyInjector.
     */
    public DependencyInjector() {
        providedObjects = new HashMap<>();
        dependencies = new HashMap<>();
    }

    /**
     * Scans the whole classpath for methods annotated with {@link Produces}. If found, creates a new instance of
     * the declaring class and will call the method to retrieve the object and register it as a dependency. If the method
     * is declared as static, no instance will be created. Use {@link #registerProvider(Object)} if no new instance of
     * the declaring class should be created.
     *
     * @param packages package(s) to exclusively scan
     * @param clazz    a class of the classpath to scan
     */
    public void index(@NotNull Class<?> clazz, @NotNull String... packages) {
        log.debug("Indexing dependency providers...");

        FilterBuilder filter = new FilterBuilder();
        for (String pkg : packages) {
            filter.includePackage(pkg);
        }

        ConfigurationBuilder config = new ConfigurationBuilder()
                .setScanners(Scanners.SubTypes, Scanners.MethodsAnnotated)
                .setUrls(ClasspathHelper.forClass(clazz))
                .filterInputsBy(filter);
        Reflections reflections = new Reflections(config);

        Set<Method> methods = reflections.getMethodsAnnotatedWith(Produces.class);
        for (Method method : methods) {
            log.debug("Found producer {}", method.getName());
            if (method.getParameterTypes().length != 0) {
                log.error("An error has occurred! Skipping Producer {}", method,
                        new IllegalArgumentException("Producer method must not have parameters!"));
                continue;
            }
            Object instance;
            if (Modifier.isStatic(method.getModifiers())) {
                instance = null;
            } else {
                try {
                    instance = method.getDeclaringClass().getConstructors()[0].newInstance();
                } catch (Exception e) {
                    log.error("Unable to create provider instance!", e);
                    continue;
                }
            }

            try {
                method.setAccessible(true);
                Object object = method.invoke(instance);
                providedObjects.put(object.getClass(), object);
                log.debug("Registered {} for {}", object, object.getClass());
            } catch (Exception e) {
                log.error("Unable to access method {}", method, e);
            }
        }
    }

    /**
     * Takes an instance of a class and scans it for methods annotated with {@link Produces}. If found, will call
     * the method to retrieve the object and register it as a dependency.
     *
     * @param provider instance of the class to scan
     */
    public void registerProvider(@NotNull Object provider) {
        log.debug("Indexing dependency providers...");
        for (Method method : provider.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Produces.class)) {
                continue;
            }
            log.debug("Found producer {}", method.getName());
            if (method.getParameterTypes().length != 0) {
                log.error("An error has occurred! Skipping Producer {}", method,
                        new IllegalArgumentException("Producer method must not have parameters!"));
                continue;
            }
            try {
                method.setAccessible(true);
                Object object = method.invoke(provider);
                providedObjects.put(object.getClass(), object);
                log.debug("Registered {} for {}", object, object.getClass());
            } catch (Exception e) {
                log.error("Unable to access method {}", method, e);
            }
        }
    }

    /**
     * Registers fields inside a class as dependencies.
     *
     * @param clazz  the declaring class
     * @param fields the dependencies to register
     */
    public void registerDependencies(@NotNull Class<?> clazz, @NotNull List<Field> fields) {
        dependencies.put(clazz, fields);
    }

    /**
     * Injects all registered dependencies with the corresponding value. If no value is present {@code null} gets injected.
     */
    public void inject(Object instance) {
        dependencies.getOrDefault(instance.getClass(), Collections.emptyList()).forEach(field -> {
            try {
                field.setAccessible(true);
                field.set(instance, providedObjects.get(field.getType()));
            } catch (IllegalAccessException e) {
                log.error("Unable to inject field {}", field, e);

            }
        });
    }
}
