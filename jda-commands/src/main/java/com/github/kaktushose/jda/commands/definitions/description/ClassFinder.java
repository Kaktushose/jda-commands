package com.github.kaktushose.jda.commands.definitions.description;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.description.reflective.ReflectiveClassFinder;
import com.github.kaktushose.jda.commands.extension.Implementation;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/// [ClassFinder]s provide instances of [Class] that will be scanned for [Interaction]s
public non-sealed interface ClassFinder extends Implementation.ExtensionImplementable {

    /// @return the classes to be searched for [Interaction]
    @NotNull
    Iterable<Class<?>> find();

    /// This provides a reflections based implementation of [ClassFinder]
    ///
    /// @param baseClass The [Class] providing the used [ClassLoader]
    /// @param packages a list of packages that should be scanned
    @NotNull
    static ClassFinder reflective(@NotNull Class<?> baseClass, @NotNull String... packages) {
        return new ReflectiveClassFinder(baseClass, packages);
    }

    /// This provides an array backed implementation of [ClassFinder] that just returns the explicitly stated classes.
    /// @param classes the classes to be scanned
    @NotNull
    static ClassFinder explicit(@NotNull Class<?>... classes) {
        return () -> Arrays.asList(classes);
    }
}
