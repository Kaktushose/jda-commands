package com.github.kaktushose.jda.commands.definitions.reflective;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.Registry;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.function.Predicate;

public final class ReflectRegistry implements Registry {

    private static final Logger log = LoggerFactory.getLogger(ReflectRegistry.class);
    private final Reflections reflections;
    private Set<Definition> definitions;

    public ReflectRegistry(Class<?> clazz, String... packages) {
        FilterBuilder filter = new FilterBuilder();
        for (String pkg : packages) {
            filter.includePackage(pkg);
        }

        ConfigurationBuilder config = new ConfigurationBuilder()
                .setScanners(Scanners.SubTypes, Scanners.TypesAnnotated)
                .setUrls(ClasspathHelper.forClass(clazz))
                .filterInputsBy(filter);
        reflections = new Reflections(config);
    }

    @Override
    public void index() {
        Set<Class<?>> controllerSet = reflections.getTypesAnnotatedWith(Interaction.class);

        for (Class<?> aClass : controllerSet) {
            log.debug("Found interaction controller {}", aClass.getName());

            // TODO build definitions
        }

        log.debug("Successfully registered {} interaction controller(s) with a total of {} interaction(s)!",
                controllerSet.size(),
                definitions.size());
    }

    @Override
    public <T extends Definition> T find(Class<T> type, boolean internalError, Predicate<T> predicate) {
        return definitions.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .filter(predicate)
                .findFirst()
                .orElseThrow(() -> internalError
                        ? new IllegalStateException("No interaction found! Please report this error the the devs of jda-commands.")
                        : new IllegalArgumentException("No interaction found! Please check that the referenced interaction method exists.")
                );
    }
}
