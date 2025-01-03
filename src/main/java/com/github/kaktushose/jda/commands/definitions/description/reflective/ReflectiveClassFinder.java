package com.github.kaktushose.jda.commands.definitions.description.reflective;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import org.jetbrains.annotations.ApiStatus;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

// temp class
@ApiStatus.Internal
public class ReflectiveClassFinder {

    public static Iterable<Class<?>> find(Class<?> clazz, String[] packages) {
        var filter = new FilterBuilder();
        for (String pkg : packages) {
            filter.includePackage(pkg);
        }

        var config = new ConfigurationBuilder()
                .setScanners(Scanners.SubTypes, Scanners.TypesAnnotated)
                .setUrls(ClasspathHelper.forClass(clazz))
                .filterInputsBy(filter);
        var reflections = new Reflections(config);
        return reflections.getTypesAnnotatedWith(Interaction.class);
    }
}
