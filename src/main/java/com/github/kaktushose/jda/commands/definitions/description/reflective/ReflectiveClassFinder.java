package com.github.kaktushose.jda.commands.definitions.description.reflective;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

// temp class
@ApiStatus.Internal
public class ReflectiveClassFinder implements ClassFinder {

    private final Class<?> clazz;
    private final String[] packages;

    public ReflectiveClassFinder(Class<?> clazz, String[] packages) {
        this.clazz = clazz;
        this.packages = packages;
    }

    @Override
    public @NotNull Iterable<Class<?>> find() {
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
